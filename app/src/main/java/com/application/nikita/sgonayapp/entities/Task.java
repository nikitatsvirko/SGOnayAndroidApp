package com.application.nikita.sgonayapp.entities;

import java.io.Serializable;

import static com.application.nikita.sgonayapp.utils.Constants.EMPTY_STRING;

public class Task implements Serializable {

    private String id;
    private String description;
    private String text;
    private String cost;
    private String imageUrl = EMPTY_STRING;

    public Task(String id, String description, String text){
        this.id = id;
        this.description = description;
        this.text = text;
        this.cost = "";
    }

    public Task(String id, String description, String text, String cost, String imgUrl){
        this.id = id;
        this.description = description;
        this.text = text;
        this.cost = cost;
        this.imageUrl = imgUrl;
    }

    public String getImageUrl() {

        return imageUrl;
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

    public String getCost() {

        return cost;
    }
}
