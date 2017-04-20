package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.application.nikita.sgonayapp.helper.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.application.nikita.sgonayapp.app.AppConfig.*;

/**
 * Created by Nikita on 06.03.2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mLoginText;
    private EditText mPasswordText;
    private Button mSignInButton;
    private TextView mSignUpTextView;
    private ProgressDialog mProgressDialog;
    private SessionManager mSession;
    private SQLiteHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(R.string.title_activity_login);
        mLoginText = (EditText)findViewById(R.id.login_text);
        mPasswordText = (EditText) findViewById(R.id.password_text);
        mSignInButton = (Button) findViewById(R.id.signin_button);
        mSignUpTextView = (TextView)findViewById(R.id.link_signup);

        mSignUpTextView.setClickable(true);
        mSignUpTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setCancelable(false);

        db = new SQLiteHandler(getApplicationContext());

        mSession = new SessionManager(getApplicationContext());

       if (mSession.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, GamesActivity.class);
            startActivity(intent);
            finish();
       }
    }

    public void logIn(View view) {
        String team = mLoginText.getText().toString().trim();
        String password = mPasswordText.getText().toString().trim();

        if (!team.isEmpty() && !password.isEmpty()) {
            checkLogin(team, password);
        } else {
            Toast.makeText(getApplicationContext(),
                    R.string.empty_forms, Toast.LENGTH_LONG).show();
        }
    }

    private void checkLogin(final String team, final String password) {
        mProgressDialog.setMessage(getString(R.string.waiting_text));
        showDialog();

        final String requestBody = Uri.encode("\"Login\":\"" + team + "\"," +
                "\"Password\":\"" + password + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_LOGIN, requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();

                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            String retParameter = responseObject.getString(RETURN_PARAMETER_STRING);

                            setUserLoggedIn(retParameter, team);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                            hideDialog();
                        }
                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void showDialog() {
        if (!mProgressDialog.isShowing())
            mProgressDialog.show();
    }

    private void hideDialog() {
        if (mProgressDialog.isShowing())
            mProgressDialog.dismiss();
    }

    private void setUserLoggedIn(String retParameter, String team) {
        if (!retParameter.contains("false")) {

            mSession.setLogin(true);

            db.addUser(team, retParameter);

            Intent intent = new Intent(LoginActivity.this, GamesActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_credentials,
                    Toast.LENGTH_LONG).show();
        }
    }
}
