package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Game {
    String name;
    String date;
    String timeOut;
    int image;      // why int ???

    public Game(String name, String date, String timeOut, int image){
        this.name = name;
        this.date = date;
        this.timeOut = timeOut;
        this.image = image;
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

}
