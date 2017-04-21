package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Game {
    private String mNumber;
    private String mDate;
    private String mTimeOut;
    private String mTitle;
    private String mScheme;
    private int mImage;

    public Game(String number, String date, String timeOut, String scheme){
        this.mNumber = number;
        this.mDate = date;
        this.mTimeOut = timeOut;
        this.mTitle = "";
        this.mScheme = "";
    }

    public Game(String number, String date, String timeOut, int image, String title, String scheme){
        this.mNumber = number;
        this.mDate = date;
        this.mTimeOut = timeOut;
        this.mImage = image;
        this.mTitle = title;
        this.mScheme = scheme;
    }

    public String getNumber() {

        return mNumber;
    }

    public int getImage() {

        return mImage;
    }

    public String getTimeOut() {

        return mTimeOut;
    }

    public String getDate() {

        return mDate;
    }


    public String getTitle() {

        return mTitle;
    }


    public String getScheme() {

        return mScheme;
    }
}
