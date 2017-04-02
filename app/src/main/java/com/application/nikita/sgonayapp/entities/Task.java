package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Task {
    int id;
    String title;
    String text;
    int image;

    public Task(int id, String text, int image){
        this.id = id;
        this.title = text.substring(0, 30);
        this.text = text;
        this.image = image;
    }

    public int getImage() {
        return image;
    }

    public String getText() {

        return text;
    }

    public String getTitle() {

        return title;
    }

    public int getId() {

        return id;
    }
}
