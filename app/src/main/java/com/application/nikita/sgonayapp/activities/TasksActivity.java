package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.TaskAdapter;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Task;
import com.application.nikita.sgonayapp.helper.SQLiteHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.application.nikita.sgonayapp.app.AppConfig.*;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();
    private RecyclerView mTasksRecyclerView;
    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private String mGameNumber;
    private SQLiteHandler db;
    private boolean isFinished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        mTasksRecyclerView = (RecyclerView) findViewById(R.id.tasks_list);
        mTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        db = new SQLiteHandler(getApplicationContext());
        mGameNumber = getIntent().getStringExtra("game_number");
        loadTasks(mGameNumber);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_tasks, menu);
        return true;
    }

    public void loadTasks(String number) {
        mProgressDialog.setMessage(getString(R.string.waiting_tasks_text));
        showDialog();

        final String requestBody = Uri.encode("\"game\":\"" + number + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_GET_TASKS, requestBody);

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
        for(int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            mTasks.add(new Task(object.getString("Number"),
                    object.getString("Description").replaceAll("(?:<br>)", ""),
                    object.getString("Task"),
                    object.getString("Price")));
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
        mProgressDialog.setMessage(getString(R.string.loading_txt));
        showDialog();

        final String requestBody = Uri.encode("\"game\":\"" + mGameNumber + "\",\"Key\":\"" + getUid(db) + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_FINISH, requestBody);
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

    @Override
    public void onBackPressed() {}

    public void refreshOnClick(MenuItem item) {
        mTasks.clear();
        loadTasks(mGameNumber);
    }

    public void onTaskClicked(Task task) {
        Intent intent = new Intent(TasksActivity.this, AnswerActivity.class);
        intent.putExtra("id",task.getId());
        intent.putExtra("price", task.getCost());
        intent.putExtra("description", task.getDescription());
        intent.putExtra("text", task.getText());
        intent.putExtra("scheme", getIntent().getStringExtra("scheme"));
        intent.putExtra("game_number", getIntent().getStringExtra("game_number"));
        startActivity(intent);
    }
}