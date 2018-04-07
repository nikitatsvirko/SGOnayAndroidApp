package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.GameAdapter;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Game;
import com.application.nikita.sgonayapp.helper.SessionManager;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.application.nikita.sgonayapp.utils.Common.getStartGameRequestUrl;
import static com.application.nikita.sgonayapp.utils.Constants.GAME_NUMBER;
import static com.application.nikita.sgonayapp.utils.Constants.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.SCHEME;
import static com.application.nikita.sgonayapp.utils.Constants.URL_GET_GAMES;

public class GamesActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private final String TAG = GamesActivity.class.getSimpleName();
    private RecyclerView mGamesRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private GameAdapter mAdapter;
    private SessionManager mSession;
    private ProgressDialog mProgressDialog;

    private ArrayList<Game> mGames = new ArrayList<>();
    private boolean isStarted;

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_games);
        checkForUpdates();
        mProgressDialog = new ProgressDialog(GamesActivity.this);
        mProgressDialog.setCancelable(false);
        mGamesRecyclerView = (RecyclerView) findViewById(R.id.games_list_recycler_view);
        mGamesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mAdapter = new GameAdapter(getApplicationContext(), mGames, new GameAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Game game) {
                showStartDialog(game);
            }
        });
        mGamesRecyclerView.setAdapter(mAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_games_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED);

        mSession = new SessionManager(getApplicationContext());

        loadGames(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);
        return true;
    }

    @Override
    public void onRefresh() {
        mGames.clear();
        loadGames(false);
    }

    private void loadGames(boolean isFirstTime) {
        if (isFirstTime) {
            mProgressDialog.setMessage(getString(R.string.waiting_games_text));
            showDialog();
            loadGames();
        } else  {
            loadGames();
        }
    }

    public void logOut(MenuItem item) {
        if (mSession.isLoggedIn()) {
            mSession.setLogin(false);
            AppController.getInstance().getDb().deleteUsers();

            Intent intent = new Intent(GamesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.not_logged_in , Toast.LENGTH_LONG).show();
        }
    }

    public void loadGames() {
        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                URL_GET_GAMES, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            JSONArray jsonArray = responseObject.getJSONArray(RETURN_PARAMETER_STRING);

                            putDataToAdapter(jsonArray);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hideDialog();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hideDialog();
            }
        });

        AppController.getInstance().addToRequestQueue(tasksRequest);
    }

    private void showDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }


    private void putDataToAdapter(JSONArray array) throws JSONException {
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            mGames.add(new Game(object.getString("Number"),
                    object.getString("Title"),
                    object.getString("Timeout"),
                    object.getString("Date"),
                    object.getString("Scheme")));
        }

        mAdapter.notifyDataSetChanged();
    }

    public void showStartDialog(final Game game) {
        AlertDialog.Builder builder =
                new AlertDialog.Builder(GamesActivity.this);
        builder.setTitle(R.string.start_game_title);
        builder.setMessage(getString(R.string.start_game_message));
        builder.setPositiveButton(R.string.start_text,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(game.getNumber(), game.getScheme());

                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startGame(final String mGameNumber, final String scheme) {
        final String requestURL = getStartGameRequestUrl(mGameNumber);
        Log.d(TAG, "URL: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            isStarted = responseObject.getBoolean(RETURN_PARAMETER_STRING);
                            startTasksActivity(isStarted, mGameNumber, scheme);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(tasksRequest);
    }

    private void startTasksActivity(boolean isStarted, String mGameNumber, String scheme) {
        if (isStarted) {
            Intent intent = new Intent(GamesActivity.this, TasksActivity.class);
            intent.putExtra(GAME_NUMBER, mGameNumber);
            intent.putExtra(SCHEME, scheme);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Упс", Toast.LENGTH_LONG).show();
        }
    }

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}
