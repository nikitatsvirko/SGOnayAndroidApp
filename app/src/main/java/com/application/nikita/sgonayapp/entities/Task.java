package com.application.nikita.sgonayapp.entities;

/**
 * Created by Konstantin on 02.04.2017.
 */

public class Task {

    private String id;
    private String description;
    private String text;
    private Integer cost;
    private int image;

    public Task(String id, String description, String text){
        this.id = id;
        this.description = description;
        this.text = text;
        this.cost = -1;
    }

    public Task(String id, String description, String text, Integer cost){
        this.id = id;
        this.description = description;
        this.text = text;
        this.cost = cost;
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

    public Integer getCost() {

        return cost;
    }
}
