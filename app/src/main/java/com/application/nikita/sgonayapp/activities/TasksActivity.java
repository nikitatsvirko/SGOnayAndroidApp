package com.application.nikita.sgonayapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.TaskAdapter;
import com.application.nikita.sgonayapp.entities.Task;

import java.util.ArrayList;

public class TasksActivity extends AppCompatActivity {

    static ListView mTasksList;
    static TaskAdapter mAdapter;
    ArrayList<Task> mTasks = new ArrayList<>();

    String txt1 ="ул. Петра Глебки, 5\nТорговый центр «Скала». " +
            "Поляна сказок в парке Тивали к северо-востоку от здания.\n Кто сопровождает старика?";
    String txt2 = "В месте, указанном на карте, находится своеобразный \"мост\" через реку\n" +
                    "Количество цилиндрических плит, расположенных на воде.";
    String txt3 = "просп. Независимости, 44.\n" +
                    "Заведение с китайскими мотивами во дворе здания.\n" +
                    "Кто повис на дереве?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        Task t1 = new Task(1, txt1, 1);
        Task t2 = new Task(2, txt2, 2);
        Task t3 = new Task(3, txt3, 3);

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

    }
}
