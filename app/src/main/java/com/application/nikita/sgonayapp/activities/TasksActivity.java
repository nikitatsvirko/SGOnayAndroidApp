package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.TaskAdapter;
import com.application.nikita.sgonayapp.app.AppConfig;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Task;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();
    private ListView mTasksList;
    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private String gameNumber = "54";
    private int countOfTasks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);
        mTasksList = (ListView) findViewById(R.id.tasks_list);

        loadTasks();

        mTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnswerActivity.setTask(mTasks.get(position));

                Intent intent = new Intent(TasksActivity.this, AnswerActivity.class);
                startActivity(intent);
            }
        });
    }

    public void loadTasks() {
        mProgressDialog.setMessage(getString(R.string.waiting_tasks_text));
        showDialog();

        String requestBody = Uri.encode("\"game\":\"" + gameNumber + "\"", AppConfig.ALLOWED_URI_CHARS);

        final String requestURL = String.format(AppConfig.URL_GET_TASKS, requestBody);
        Log.d(TAG, "Requset url: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject("response");
                            JSONArray jsonArray = responseObject.getJSONArray("retParameter");
                            countOfTasks = jsonArray.length();

                            for(int i = 0; i < countOfTasks; i++) {
                                JSONObject object = jsonArray.getJSONObject(i);
                                mTasks.add(new Task(object.getString("Number"),
                                            object.getString("Description"),
                                            object.getString("Task")));
                                Log.d(TAG, "Number: " + object.getString("Number"));
                                Log.d(TAG, "Description: " + object.getString("Description"));
                                Log.d(TAG, "Task: " + object.getString("Task"));
                            }

                            mAdapter = new TaskAdapter(getApplicationContext(), mTasks);
                            mTasksList.setAdapter(mAdapter);

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
}
