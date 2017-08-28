package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.helper.SQLiteHandler;

import org.json.JSONException;
import org.json.JSONObject;

import static com.application.nikita.sgonayapp.app.AppConfig.ALLOWED_URI_CHARS;
import static com.application.nikita.sgonayapp.app.AppConfig.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.app.AppConfig.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.app.AppConfig.URL_SEND_ANSWER;
import static com.application.nikita.sgonayapp.app.AppConfig.getUid;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class AnswerActivity extends AppCompatActivity {

    private String TAG = AnswerActivity.class.getSimpleName();
    private TextView mIdTxtView;
    private TextView mDescriptionTxtView;
    private TextView mTextTxtView;
    private TextView mPriceTxtView;
    private String mScheme;
    private String mGameNumber;
    private SQLiteHandler db;
    private EditText mAnswer;
    private String isOk;

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_answer);

        db = new SQLiteHandler(getApplicationContext());

        mGameNumber = getIntent().getStringExtra("game_number");
        mScheme = getIntent().getStringExtra("scheme");
        setUpUI();
    }

    public void onSubmitClicked(View view) {
        String answer = mAnswer.getText().toString().trim().replaceAll("#|&|\"", " ");

        if (!answer.isEmpty()) {
            sendAnswer(answer);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.no_answer, Toast.LENGTH_SHORT).show();
        }
    }

    public void setUpUI() {
        mIdTxtView = (TextView)findViewById(R.id.task_id_txt);
        mPriceTxtView = (TextView)findViewById(R.id.task_cost_txt);
        mDescriptionTxtView = (TextView)findViewById(R.id.task_description_txt);
        mTextTxtView = (TextView)findViewById(R.id.task_text_txt);
        mAnswer = (EditText)findViewById(R.id.task_answer_txt);

        switch (mScheme) {
            case "3":
                mIdTxtView.setText(getIntent().getStringExtra("id"));
                mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(getIntent().getStringExtra("text"));
                break;
            case "2":
                mIdTxtView.setText(getIntent().getStringExtra("id"));
                mPriceTxtView.setText(getString(R.string.points_text, getIntent().getStringExtra("price")));
                mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(getIntent().getStringExtra("text"));
                break;
            case "1":
                int mPrice = Integer.parseInt(getIntent().getStringExtra("price"));
                if (mPrice == 0) {
                    mIdTxtView.setText(getIntent().getStringExtra("id"));
                    mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(getIntent().getStringExtra("text"));
                }
                if (mPrice > 0) {
                    mIdTxtView.setText(getIntent().getStringExtra("id"));
                    mPriceTxtView.setText("Штраф за пропуск: " + mPrice + " мин.");
                    mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(getIntent().getStringExtra("text"));
                }
                if (mPrice < 0) {
                    mIdTxtView.setText(getIntent().getStringExtra("id"));
                    mPriceTxtView.setText("Бонус: " + Math.abs(mPrice) + " мин.");
                    mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(getIntent().getStringExtra("text"));
                }
                break;
            default:
                mIdTxtView.setText(getIntent().getStringExtra("id"));
                mPriceTxtView.setText(getIntent().getStringExtra("price"));
                mDescriptionTxtView.setText(getIntent().getStringExtra("description").replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(getIntent().getStringExtra("text"));
                break;
        }
    }

    public void sendAnswer(String answer) {
        Log.d(TAG, "Input data: " + answer);

        final String requestBody = Uri.encode("\"game\":\"" + mGameNumber +
                "\",\"Key\":\"" + getUid(db) +
                "\",\"CP\":\"" + mIdTxtView.getText() +
                "\",\"Answer\":\"" + answer + "\"",
                ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_SEND_ANSWER, requestBody);

        Log.d(TAG, "URL: " + requestURL);

        JsonObjectRequest sendAnswerRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            Log.d(TAG, "Answer: " + responseObject.toString());
                            isOk = responseObject.getString(RETURN_PARAMETER_STRING);
                            Log.d(TAG, "Answer: " + isOk);
                            if (isOk.contains("true")) {
                                Toast.makeText(getApplicationContext(), "OK!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.answer_false_txt, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });

        AppController.getInstance().addToRequestQueue(sendAnswerRequest);
    }
}
