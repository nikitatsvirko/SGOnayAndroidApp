package com.application.nikita.sgonayapp.entities;

public class Game {
    private String mNumber;
    private String mTitle;
    private String mDate;
    private String mTimeOut;
    private String mScheme;
    private int mImage;

    public Game(String number, String title, String timeOut, String date ,String scheme){
        this.mNumber = number;
        this.mTitle = title;
        this.mTimeOut = timeOut;
        this.mDate = date;
        this.mScheme = scheme;
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
