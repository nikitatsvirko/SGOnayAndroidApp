package com.application.nikita.sgonayapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.application.nikita.sgonayapp.R;

/**
 * Created by Nikita on 07.03.2017.
 */

public class GamesActivity extends AppCompatActivity {

    private static ListView mGamesList;
    private static ArrayAdapter<String> mAdapter;

    private String[] mGames = {"SGOnay #54","SGOnay #46"};

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_games);

        mGamesList = (ListView) findViewById(R.id.games_list);

        mAdapter = new ArrayAdapter<String>(this, R.layout.games_list_item, mGames);

        mGamesList.setAdapter(mAdapter);

        mGamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GamesActivity.this, TasksActivity.class);
                startActivity(intent);
            }
        });
    }

}