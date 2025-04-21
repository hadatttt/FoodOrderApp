package com.example.foodorderapp.model;

public class Restaurant {
    private final String name;
    private final float rating;
    private final int imageRes;

    public Restaurant(String name, float rating, int imageRes) {
        this.name = name;
        this.rating = rating;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public float getRating() {
        return rating;
    }

    public int getImageRes() {
        return imageRes;
    }
}
