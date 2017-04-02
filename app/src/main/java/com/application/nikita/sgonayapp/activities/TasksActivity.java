package com.application.nikita.sgonayapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.application.nikita.sgonayapp.R;

public class TasksActivity extends AppCompatActivity {

    private static ListView mTasksList;
    private static ArrayAdapter<String> mAdapter;

    private String[] mTasks = {
            "01. ул. Петра Глебки, 5\nТорговый центр «Скала». " +
            "Поляна сказок в парке Тивали к северо-востоку от здания.\n Кто сопровождает старика?",
            "02. В месте, указанном на карте, находится своеобразный \"мост\" через реку\n" +
                    "Количество цилиндрических плит, расположенных на воде.",
            "03. просп. Независимости, 44.\n" +
                    "Заведение с китайскими мотивами во дворе здания.\n" +
                    "Кто повис на дереве?"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        mTasksList = (ListView) findViewById(R.id.tasks_list);

        mAdapter = new ArrayAdapter<String>(this, R.layout.tasks_list_item, mTasks);

        mTasksList.setAdapter(mAdapter);

        mTasksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }
}
