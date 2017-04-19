package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
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

import org.json.JSONObject;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {
    private static final String TAG = TasksActivity.class.getSimpleName();
    private ListView mTasksList;
    private TaskAdapter mAdapter;
    private ArrayList<Task> mTasks = new ArrayList<>();
    private ProgressDialog mProgressDialog;
    private String gameNumber = "#54";

    String txt1 ="ул. Петра Глебки, 5\nТорговый центр «Скала». " +
            "Поляна сказок в парке Тивали к северо-востоку от здания.";
    String taskText1 = "Кто сопровождает старика?";
    String txt2 = "В месте, указанном на карте, находится своеобразный \"мост\" через реку";
    String taskText2 = "Количество цилиндрических плит, расположенных на воде.";
    String txt3 = "просп. Независимости, 44.\n" +
                    "Заведение с китайскими мотивами во дворе здания.";
    String taskText3 = "Кто повис на дереве?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        Task t1 = new Task(1, txt1, taskText1);
        Task t2 = new Task(2, txt2, taskText2);
        Task t3 = new Task(3, txt3, taskText3);

        mTasks.add(t1);
        mTasks.add(t2);
        mTasks.add(t3);

        mTasksList = (ListView) findViewById(R.id.tasks_list);

        mAdapter = new TaskAdapter(this, mTasks);

        mTasksList.setAdapter(mAdapter);

        mTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                AnswerActivity.setTask(mTasks.get(position));

                Intent intent = new Intent(TasksActivity.this, AnswerActivity.class);
                startActivity(intent);

                String string = "" + mTasks.get(position).getId();
                Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
            }
        });

        loadTasks();
    }

    public void loadTasks() {
        mProgressDialog.setMessage("Загрузка заданий...");
        showDialog();

        final String requestURL = String.format(AppConfig.URL_GET_TASKS, ("\"game\":\"" + gameNumber + "\""));
        Log.d(TAG, "Requset url: " + requestURL);

        JsonObjectRequest tasksRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, "JSON response" + response.toString());
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
