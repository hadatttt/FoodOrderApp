package com.example.foodorderapp;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private String price;
    private float rating;
    private int imageResId;

    public FoodModel(int foodId, int storeId, String name, String price, float rating, int imageResId) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageResId = imageResId;
    }

    // Getters
    public int getFoodId() {
        return foodId;
    }

    public int getStoreId() {
        return storeId;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public float getRating() {
        return rating;
    }

    public int getImageResId() {
        return imageResId;
    }
}
