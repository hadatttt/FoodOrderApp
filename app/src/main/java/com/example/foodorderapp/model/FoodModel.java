package com.example.foodorderapp.model;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private double price;
    private float rating;
    private String imageUrl;
    private int sold;
    private String category;

    public FoodModel() {
        // Required empty constructor for Firestore
    }

    public FoodModel(int foodId, int storeId, String name, double price, float rating, int imagUrl, int sold, String category) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.sold = sold;
        this.category = category;
    }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }  // Sửa thành double
    public void setPrice(double price) { this.price = price; }  // Sửa thành double

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
