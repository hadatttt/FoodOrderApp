package com.example.foodorderapp;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private String price;
    private float rating;
    private int imageResId;
    private int sold; // ✅ Thêm sold

    public FoodModel(int foodId, int storeId, String name, String price, float rating, int imageResId, int sold) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageResId = imageResId;
        this.sold = sold; // ✅ Gán sold
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

    public int getSold() {
        return sold; // ✅ Getter cho sold
    }
}
