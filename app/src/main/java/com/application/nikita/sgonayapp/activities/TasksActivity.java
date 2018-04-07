package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.TaskAdapter;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Task;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.application.nikita.sgonayapp.utils.Common.getFinisRequestUrl;
import static com.application.nikita.sgonayapp.utils.Common.getTasksRequest;
import static com.application.nikita.sgonayapp.utils.Constants.GAME_NUMBER;
import static com.application.nikita.sgonayapp.utils.Constants.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.SCHEME;
import static com.application.nikita.sgonayapp.utils.Constants.TASK_EXTRA;

public class TasksActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = TasksActivity.class.getSimpleName();
    private RecyclerView mTasksRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks;
    private ProgressDialog mProgressDialog;
    private String mGameNumber;
    private String mScheme;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        checkForUpdates();
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        mTasksRecyclerView = (RecyclerView) findViewById(R.id.tasks_list);
        mTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_tasks_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeColors(Color.RED);

        mGameNumber = getIntent().getStringExtra(GAME_NUMBER);
        mScheme = getIntent().getStringExtra(SCHEME);
        loadTasks();
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
        inflater.inflate(R.menu.menu_tasks, menu);
        return true;
    }

    @Override
    public void onBackPressed() {}

    @Override
    public void onRefresh() {
        loadTasks(mGameNumber);
        updateTasks();
    }

    public void onTaskClicked(Task task) {
        Bundle extra = new Bundle();
        extra.putSerializable(TASK_EXTRA, task);

        Intent intent = new Intent(TasksActivity.this, AnswerActivity.class);
        intent.putExtras(extra);
        intent.putExtra(SCHEME, mScheme);
        intent.putExtra(GAME_NUMBER, mGameNumber);
        startActivity(intent);
    }

    private void updateTasks() {
        loadTasks(mGameNumber);
    }

    private void loadTasks() {
        showDialog(getString(R.string.waiting_tasks_text));
        loadTasks(mGameNumber);
    }

    public void loadTasks(String number) {
        final String requestURL = getTasksRequest(number);

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

    private void showDialog(String message) {
        mProgressDialog.setMessage(message);
        showDialog();
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
        mTasks = new ArrayList<>();
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            mTasks.add(new Task(object.getString("Number"),
                    object.getString("Description").replaceAll("(?:<br>)", ""),
                    object.getString("Task"),
                    object.getString("Price"),
                    object.getString("IMG")));
        }

        mAdapter = new TaskAdapter(getApplicationContext(), mTasks, new TaskAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Task task) {
                onTaskClicked(task);
            }
        });
        mTasksRecyclerView.setAdapter(mAdapter);
    }

    public void finishGame(MenuItem item) {
        showDialog(getString(R.string.loading_txt));

        final String requestURL = getFinisRequestUrl(mGameNumber);
        Log.d(TAG, "URL: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            isFinished = responseObject.getBoolean(RETURN_PARAMETER_STRING);

                            finishGame(isFinished);
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

    private void finishGame(boolean isFinished) {
        if (isFinished) {
            finish();
            super.onBackPressed();
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