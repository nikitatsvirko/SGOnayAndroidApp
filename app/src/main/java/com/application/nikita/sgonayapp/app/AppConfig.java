package com.application.nikita.sgonayapp.app;

import com.application.nikita.sgonayapp.helper.SQLiteHandler;

import java.util.Map;

public class AppConfig {
    public static String getUid(SQLiteHandler db) {
        Map<String, String> map = db.getUserDetails();

        return map.get("uid");
    }
}
