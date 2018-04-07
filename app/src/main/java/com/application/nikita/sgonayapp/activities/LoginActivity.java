package com.application.nikita.sgonayapp.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

import net.hockeyapp.android.CrashManager;
import net.hockeyapp.android.UpdateManager;

import org.json.JSONException;
import org.json.JSONObject;

import static com.application.nikita.sgonayapp.app.AppConfig.*;
import static com.application.nikita.sgonayapp.utils.Constants.ALLOWED_URI_CHARS;
import static com.application.nikita.sgonayapp.utils.Constants.RESPONSE_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.RETURN_PARAMETER_STRING;
import static com.application.nikita.sgonayapp.utils.Constants.URL_LOGIN;

/**
 * Created by Nikita on 06.03.2017.
 */

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = LoginActivity.class.getSimpleName();
    private EditText mLoginText;
    private EditText mPasswordText;
    private Button mSignInButton;
    private TextView mSignUpTextView;
    private SessionManager mSession;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(R.string.title_activity_login);
        checkForUpdates();
        mLoginText = (EditText)findViewById(R.id.login_text);
        mPasswordText = (EditText) findViewById(R.id.password_text);
        mSignInButton = (Button) findViewById(R.id.signin_button);
        mSignUpTextView = (TextView)findViewById(R.id.link_signup);

        mSignUpTextView.setClickable(true);
        mSignUpTextView.setMovementMethod(LinkMovementMethod.getInstance());

        mSession = new SessionManager(getApplicationContext());

       if (mSession.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, GamesActivity.class);
            startActivity(intent);
            finish();
       }
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
        final String requestBody = Uri.encode("\"Login\":\"" + team + "\"," +
                "\"Password\":\"" + password + "\"", ALLOWED_URI_CHARS);
        final String requestURL = String.format(URL_LOGIN, requestBody);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject responseObject = response.getJSONObject(RESPONSE_STRING);
                            String retParameter = responseObject.getString(RETURN_PARAMETER_STRING);

                            setUserLoggedIn(retParameter, team);
                            Toast.makeText(getApplicationContext(), R.string.signed_in_text, Toast.LENGTH_LONG).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d(TAG, "JSON error: " + e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            VolleyLog.d(TAG, "Error: " + error.getMessage());
                        }
                });

        AppController.getInstance().addToRequestQueue(jsonObjectRequest);
    }

    private void setUserLoggedIn(String retParameter, String team) {
        if (!retParameter.contains("false")) {

            mSession.setLogin(true);

            AppController.getInstance().getDb().addUser(team, retParameter);

            Intent intent = new Intent(LoginActivity.this, GamesActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(getApplicationContext(), R.string.wrong_credentials,
                    Toast.LENGTH_LONG).show();
        }
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
