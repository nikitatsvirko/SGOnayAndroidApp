package com.application.nikita.sgonayapp.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.application.nikita.sgonayapp.R;
import com.application.nikita.sgonayapp.app.AppController;
import com.application.nikita.sgonayapp.entities.Task;
import com.application.nikita.sgonayapp.popup.PhotoFullPopupWindow;

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.application.nikita.sgonayapp.utils.Common.getSendAnswerRequestUrl;
import static com.application.nikita.sgonayapp.utils.Constants.EMPTY_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.GAME_NUMBER;
import static com.application.nikita.sgonayapp.utils.Constants.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.SCHEME;
import static com.application.nikita.sgonayapp.utils.Constants.TASK_EXTRA;

public class AnswerActivity extends AppCompatActivity {

    private String TAG = AnswerActivity.class.getSimpleName();
    private TextView mIdTxtView;
    private TextView mDescriptionTxtView;
    private TextView mTextTxtView;
    private TextView mPriceTxtView;
    private ImageView mImage;
    private EditText mAnswer;
    private String mScheme;
    private String mGameNumber;
    private Task mTask;
    private String isOk;

    private final String SCHEME_ONE = "1";
    private final String SCHEME_TWO = "2";
    private final String SCHEME_THREE = "3";

    @Override
    public void onCreate(Bundle onSavedInstantState) {
        super.onCreate(onSavedInstantState);
        setContentView(R.layout.activity_answer);
        checkForUpdates();
        getIntentExtra();
        setUpUI();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkForCrashes();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterManagers();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterManagers();
    }

    private void getIntentExtra() {
        mGameNumber = getIntent().getStringExtra(GAME_NUMBER);
        mScheme = getIntent().getStringExtra(SCHEME);
        if (getIntent().getExtras() != null) {
            mTask = (Task) getIntent().getExtras().getSerializable(TASK_EXTRA);
        }
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
        mAnswer = (EditText) findViewById(R.id.task_answer_txt);
        mImage = (ImageView) findViewById(R.id.task_image);
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new PhotoFullPopupWindow(getApplicationContext(), R.layout.popup_photo_full, view, mTask.getImageUrl(), null);

            }
        });

        if (!mTask.getImageUrl().equals(EMPTY_STRING)) {
            mImage.setVisibility(View.VISIBLE);
            AppController.getInstance().loadImageIntoView(mImage, mTask.getImageUrl());
        }

        switch (mScheme) {
            case SCHEME_THREE:
                mIdTxtView.setText(mTask.getId());
                mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(mTask.getText());
                break;
            case SCHEME_TWO:
                mIdTxtView.setText(mTask.getId());
                mPriceTxtView.setText(getString(R.string.points_text, mTask.getCost()));
                mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(mTask.getText());
                break;
            case SCHEME_ONE:
                int mPrice = Integer.parseInt(mTask.getCost());
                if (mPrice == 0) {
                    mIdTxtView.setText(mTask.getId());
                    mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(mTask.getText());
                }
                if (mPrice > 0) {
                    mIdTxtView.setText(mTask.getId());
                    mPriceTxtView.setText("Штраф за пропуск: " + mPrice + " мин.");
                    mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(mTask.getText());
                }
                if (mPrice < 0) {
                    mIdTxtView.setText(mTask.getId());
                    mPriceTxtView.setText("Бонус: " + Math.abs(mPrice) + " мин.");
                    mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                    mTextTxtView.setText(mTask.getText());
                }
                break;
            default:
                mIdTxtView.setText(mTask.getId());
                mPriceTxtView.setText(mTask.getCost());
                mDescriptionTxtView.setText(mTask.getDescription().replaceAll("(?:<br>)", ""));
                mTextTxtView.setText(mTask.getText());
                break;
        }
    }

    public void sendAnswer(String answer) {
        Log.d(TAG, "Input data: " + answer);

        final String requestURL = getSendAnswerRequestUrl(answer, mGameNumber, String.valueOf(mIdTxtView.getText()));

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

    private void checkForCrashes() {
        CrashManager.register(this);
    }

    private void checkForUpdates() {
        // Remove this for store builds!
        UpdateManager.register(this);
    }

    private void unregisterManagers() {
        UpdateManager.unregister();
    }
}
