package com.application.nikita.sgonayapp.helper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.*;
import android.util.Log;

/**
 * Created by niktia on 16.4.17.
 */

public class SessionManager {

    private static String TAG = SessionManager.class.getSimpleName();

    SharedPreferences mPreferences;

    Editor mEditor;
    Context mContext;

    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "UserLoginStatus";

    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this.mContext = context;
        mPreferences = mContext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        mEditor = mPreferences.edit();
    }

    public void setLogin(boolean isLoggedIn) {
        mEditor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        mEditor.commit();
        mEditor.apply();

        Log.d(TAG, "User login session modified");
    }

    public boolean isLoggedIn() {
        return mPreferences.getBoolean(KEY_IS_LOGGEDIN, false);
    }
}
