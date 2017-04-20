package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Game {
    String name;
    String date;
    String timeOut;
    String title;
    String scheme;
    int image;

    public Game(String name, String date, String timeOut, int image){
        this.name = name;
        this.date = date;
        this.timeOut = timeOut;
        this.image = image;
        this.title = "";
        this.scheme = "";
    }

    public Game(String name, String date, String timeOut, int image, String title, String scheme){
        this.name = name;
        this.date = date;
        this.timeOut = timeOut;
        this.image = image;
        this.title = title;
        this.scheme = scheme;
    }

    public String getName() {

        return name;
    }

    public int getImage() {

        return image;
    }

    public String getTimeOut() {

        return timeOut;
    }

    public String getDate() {

        return date;
    }


    public String getTitle() {

        return title;
    }


    public String getScheme() {

        return scheme;
    }
}
