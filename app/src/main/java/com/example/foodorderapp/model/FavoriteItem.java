package com.example.foodorderapp.model;

public class FavoriteItem {
    // FavoriteItem model class
    private final String name;
    private final String restaurant;
    private final int imageRes;

    public FavoriteItem(String name, String restaurant, int imageRes) {
        this.name = name;
        this.restaurant = restaurant;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public String getRestaurant() {
        return restaurant;
    }

    public int getImageRes() {
        return imageRes;
    }
}
