package com.application.nikita.sgonayapp.activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.GameAdapter;
import com.application.nikita.sgonayapp.adapters.TaskAdapter;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Game;
import com.application.nikita.sgonayapp.entities.Task;
import com.application.nikita.sgonayapp.helper.SQLiteHandler;
import com.application.nikita.sgonayapp.helper.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.application.nikita.sgonayapp.app.AppConfig.ALLOWED_URI_CHARS;
import static com.application.nikita.sgonayapp.app.AppConfig.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.app.AppConfig.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.app.AppConfig.URL_FINISH;
import static com.application.nikita.sgonayapp.app.AppConfig.URL_GET_GAMES;
import static com.application.nikita.sgonayapp.app.AppConfig.URL_GET_TASKS;
import static com.application.nikita.sgonayapp.app.AppConfig.URL_START_GAME;
import static com.application.nikita.sgonayapp.app.AppConfig.getUid;

/**
 * Created by Nikita on 07.03.2017.
 */

public class GamesActivity extends AppCompatActivity {
    private final String TAG = GamesActivity.class.getSimpleName();
    private ListView mGamesList;
    private int mGamesCount;
    private GameAdapter mAdapter;
    private SessionManager mSession;
    private SQLiteHandler db;
    private ProgressDialog mProgressDialog;

    private ArrayList<Game> mGames = new ArrayList<>();
    private boolean isStarted;

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_games);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mGamesList = (ListView) findViewById(R.id.games_list);
        mSession = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        loadGames();

        mGamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            showStartDialog(position);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_game, menu);
        return true;
    }

    public void logOut(MenuItem item) {
        if (mSession.isLoggedIn()) {
            mSession.setLogin(false);
            db.deleteUsers();

            Intent intent = new Intent(GamesActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.not_logged_in , Toast.LENGTH_LONG).show();
        }
    }

    public void loadGames() {
        mProgressDialog.setMessage(getString(R.string.waiting_games_text));
        showDialog();


        final String requestURL = URL_GET_GAMES;

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
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
        mGamesCount = array.length();

        for(int i = 0; i < mGamesCount; i++) {
            JSONObject object = array.getJSONObject(i);
            mGames.add(new Game(object.getString("Number"),
                    object.getString("Title"),
                    object.getString("Timeout"),
                    object.getString("Date"),
                    object.getString("Scheme")));
        }

        mAdapter = new GameAdapter(getApplicationContext(), mGames);
        mGamesList.setAdapter(mAdapter);
    }

    public void refreshOnClick(MenuItem item) {
        mGames.clear();
        loadGames();
    }

    public void showStartDialog(final int position) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(GamesActivity.this);
        builder.setTitle(R.string.start_game_title);
        builder.setMessage(getString(R.string.start_game_message));
        builder.setPositiveButton(R.string.start_text,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startGame(mGames.get(position).getNumber());

                        Intent intent = new Intent(GamesActivity.this, TasksActivity.class);
                        intent.putExtra("game_number", mGames.get(position).getNumber());
                        intent.putExtra("scheme", mGames.get(position).getScheme());
                        startActivity(intent);
                        dialog.cancel();
                    }
                });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public void startGame(String mGameNumber) {
        mProgressDialog.setMessage(getString(R.string.loading_txt));
        showDialog();

        final String requestBody = Uri.encode("\"game\":\"" + mGameNumber + "\",\"Key\":\"" + getUid(db) + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_START_GAME, requestBody);
        Log.d(TAG, "URL: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            isStarted = responseObject.getBoolean(RETURN_PARAMETER_STRING);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        hideDialog();

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
}
