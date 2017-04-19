package com.application.nikita.sgonayapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.adapters.GameAdapter;
import com.application.nikita.sgonayapp.entities.Game;
import com.application.nikita.sgonayapp.helper.SQLiteHandler;
import com.application.nikita.sgonayapp.helper.SessionManager;

import java.util.ArrayList;

/**
 * Created by Nikita on 07.03.2017.
 */

public class GamesActivity extends AppCompatActivity {

    private ListView mGamesList;
    private GameAdapter mAdapter;
    private SessionManager mSession;
    private SQLiteHandler db;
    private ArrayList<Game> mGames = new ArrayList<>();

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_games);

        Game g1 = new Game("SGOnay #46", "Февраль-март 2017", "1 час 20 минут", 2);
        Game g2 = new Game("SGOnay #47", "Март-апрель 2017", "50 минут", 3);

        mGames.add(g1);
        mGames.add(g2);

        mGamesList = (ListView) findViewById(R.id.games_list);

        mAdapter = new GameAdapter(this, mGames);

        mGamesList.setAdapter(mAdapter);

        mSession = new SessionManager(getApplicationContext());
        db = new SQLiteHandler(getApplicationContext());

        mGamesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(GamesActivity.this, TasksActivity.class);
                startActivity(intent);
                //String string = mGames.get(position).getName();
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
}
