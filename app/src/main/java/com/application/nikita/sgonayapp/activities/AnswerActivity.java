package com.application.nikita.sgonayapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.entities.Task;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class AnswerActivity extends AppCompatActivity {

    private static Task task;
    private AdView mAdView;

    public static void setTask(Task task) {
        AnswerActivity.task = task;
    }

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_answer);

        String text = "" + task.getId() + ". " + task.getDescription() + ". " + task.getText();

        ((TextView) findViewById(R.id.answer_text_view)).setText(text);

        MobileAds.initialize(this,
                "ca-app-pub-3982529297840521~1060539962");
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3940256099942544/6300978111");
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


    }

    public void onClicked(View view) {
        Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_SHORT).show();
    }

}
