package com.application.nikita.sgonayapp.app;

import com.application.nikita.sgonayapp.helper.SQLiteHandler;

import java.util.Map;

/**
 * Created by niktia on 16.4.17.
 */

public class AppConfig {
    //Base URL's
    public static final String URL_LOGIN = "http://sgonay.96.lt/api/?apitest.login={%s}";
    public static final String URL_GET_TASKS = "http://sgonay.96.lt/api/?apitest.gettasks={%s}";
    public static final String URL_START_GAME = "http://sgonay.96.lt/api/?apitest.startgame={%s}";
    public static final String URL_FINISH = "http://sgonay.96.lt/api/?apitest.finish={%s}";
    public static final String URL_SEND_ANSWER = "http://sgonay.96.lt/api/?apitest.sendansw={%s}";
    public static final String URL_GET_GAMES = "http://sgonay.96.lt/api/?apitest.getgames={}";
    public static final String ALLOWED_URI_CHARS = "\"@#&=*+-_.,:!?()/~'%";
    public static final String RESPONSE_STRING = "response";
    public static final String RETURN_PARAMETER_STRING = "retParameter";
    public static final String SGONAY_TEXT = "SGOnay#";
    public static final String MINUTES_TEXT = " мин.";

    public static String getUid(SQLiteHandler db) {
        Map<String, String> map = db.getUserDetails();

        return map.get("uid");
    }
}
