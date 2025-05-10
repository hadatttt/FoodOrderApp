package com.example.foodorderapp.model;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private String price;
    private float rating;
    private int imageResId;
    private int sold;
    private String category;

    public FoodModel() {
        // Required empty constructor for Firestore
    }

    public FoodModel(int foodId, int storeId, String name, String price, float rating, int imageResId, int sold, String category) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageResId = imageResId;
        this.sold = sold;
        this.category = category;
    }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getImageResId() { return imageResId; }
    public void setImageResId(int imageResId) { this.imageResId = imageResId; }

    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
