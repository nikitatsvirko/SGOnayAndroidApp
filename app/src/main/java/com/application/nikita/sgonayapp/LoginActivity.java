package com.application.nikita.sgonayapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Nikita on 06.03.2017.
 */

public class LoginActivity extends AppCompatActivity {

    private static EditText mLoginText;
    private static EditText mPasswordText;
    private static Button mSignInButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mLoginText = (EditText)findViewById(R.id.login_text);
        mPasswordText = (EditText) findViewById(R.id.password_text);
        mSignInButton = (Button) findViewById(R.id.signin_button);

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }
}
