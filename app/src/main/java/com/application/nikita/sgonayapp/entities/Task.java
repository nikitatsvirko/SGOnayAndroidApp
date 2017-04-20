package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Task {

    private String id;
    private String description;
    private String text;
    private int image;

    public Task(String id, String description, String text){
        this.id = id;
        this.description = description;
        this.text = text;
    }

    public int getImage() {
        return image;
    }

    public String getText() {

        return text;
    }

    public String getDescription() {

        return description;
    }

    public String getId() {

        return id;
    }
}
