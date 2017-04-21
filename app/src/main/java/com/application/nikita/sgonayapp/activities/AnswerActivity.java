package com.application.nikita.sgonayapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.application.nikita.sgonayapp.R;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class AnswerActivity extends AppCompatActivity {

    private String TAG = AnswerActivity.class.getSimpleName();
    private TextView mIdTxtView;
    private TextView mDescriptionTxtView;
    private TextView mTextTxtView;
    private TextView mPriceTxtView;


    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_answer);
        mIdTxtView = (TextView)findViewById(R.id.task_id_txt);
        mDescriptionTxtView = (TextView)findViewById(R.id.task_description_txt);
        mTextTxtView = (TextView)findViewById(R.id.task_text_txt);

        mIdTxtView.setText(getIntent().getStringExtra("id"));
        mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
        mTextTxtView.setText(getIntent().getStringExtra("text"));

        Log.d(TAG,"ID: " + getIntent().getStringExtra("id"));
        Log.d(TAG,"Description: " + getIntent().getStringExtra("description"));
        Log.d(TAG,"Text: " + getIntent().getStringExtra("text"));
    }

    public void onClicked(View view) {
        Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_SHORT).show();
    }
}
