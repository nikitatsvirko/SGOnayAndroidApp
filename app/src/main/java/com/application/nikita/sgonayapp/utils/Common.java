package com.application.nikita.sgonayapp.utils;


import android.net.Uri;

import com.application.nikita.sgonayapp.app.AppController;

import static com.application.nikita.sgonayapp.app.AppConfig.getUid;
import static com.application.nikita.sgonayapp.utils.Constants.ALLOWED_URI_CHARS;
import static com.application.nikita.sgonayapp.utils.Constants.URL_FINISH;
import static com.application.nikita.sgonayapp.utils.Constants.URL_GET_TASKS;
import static com.application.nikita.sgonayapp.utils.Constants.URL_SEND_ANSWER;
import static com.application.nikita.sgonayapp.utils.Constants.URL_START_GAME;

public class Common {

    public static String getTasksRequest(String gameNumber) {
        String body = getTasksRequestBody(gameNumber);
        return String.format(URL_GET_TASKS, body);
    }

    public static String getFinisRequestUrl(String gameNumber) {
        String body = getFinishRequestBody(gameNumber);
        return String.format(URL_FINISH, body);
    }

    public static String getSendAnswerRequestUrl(String answer, String gameNumber, String cpId) {
        String body = getSendAnswerRequestBody(answer, gameNumber, cpId);
        return String.format(URL_SEND_ANSWER, body);
    }

    public static String getStartGameRequestUrl(String gameNumber) {
        String body = Uri.encode("\"game\":\"" + gameNumber + "\",\"Key\":\"" +
                getUid(AppController.getInstance().getDb()) + "\"", ALLOWED_URI_CHARS);
        return String.format(URL_START_GAME, body);
    }

    private static String getSendAnswerRequestBody(String answer, String gameNumber, String cpId) {
        return Uri.encode("\"game\":\"" + gameNumber +
                        "\",\"Key\":\"" + getUid(AppController.getInstance().getDb()) +
                        "\",\"CP\":\"" + cpId +
                        "\",\"Answer\":\"" + answer + "\"",
                ALLOWED_URI_CHARS);
    }

    private static String getFinishRequestBody(String gameNumber) {
        return Uri.encode("\"game\":\"" + gameNumber + "\",\"Key\":\"" +
                getUid(AppController.getInstance().getDb()) + "\"", ALLOWED_URI_CHARS);
    }

    private static String getTasksRequestBody(String gameNumber) {
        return Uri.encode("\"game\":\"" + gameNumber + "\"", ALLOWED_URI_CHARS);
    }
}
