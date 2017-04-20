package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
    private ListView mTasksList;
    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private String gameNumber = "54";
    private int countOfTasks;
    private SQLiteHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mTasksList = (ListView) findViewById(R.id.tasks_list);
        db = new SQLiteHandler(getApplicationContext());

        loadTasks(gameNumber);

        mTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnswerActivity.setTask(mTasks.get(position));

                Intent intent = new Intent(TasksActivity.this, AnswerActivity.class);
                startActivity(intent);
            }
        });
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
        countOfTasks = array.length();

        for(int i = 0; i < countOfTasks; i++) {
            JSONObject object = array.getJSONObject(i);
            mTasks.add(new Task(object.getString("Number"),
                    object.getString("Description"),
                    object.getString("Task")));
        }

        mAdapter = new TaskAdapter(getApplicationContext(), mTasks);
        mTasksList.setAdapter(mAdapter);
    }

    public void finishGame(MenuItem item) {
        mProgressDialog.setMessage(getString(R.string.loading_txt));
        showDialog();

        final String requestBody = Uri.encode("\"game\":\"" + gameNumber + "\"Key\":\"" + getUid(db) + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_FINISH, requestBody);
        Log.d(TAG, "URL: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            boolean retParameter = responseObject.getBoolean(RETURN_PARAMETER_STRING);
                            Log.d(TAG, "RESULT: " + retParameter);
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