package com.example.foodorderapp.model;

import java.util.Map;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private double price;  // giá bắt đầu, khởi điểm
    private float rating;
    private int sold;
    private String category;
    private String imageUrl;
    private String caption;
    private Map<String, Double> sizePrices; // ✅ Giá theo size: S, M, L

    // Constructor mặc định (dùng cho Firestore)
    public FoodModel() {}

    // Constructor đầy đủ
    public FoodModel(int foodId, int storeId, String name, double price, float rating,
                     int sold, String category, String imageUrl, String caption,
                     Map<String, Double> sizePrices) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.sold = sold;
        this.category = category;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.sizePrices = sizePrices;
    }

    // Getters và Setters
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public Map<String, Double> getSizePrices() { return sizePrices; }
    public void setSizePrices(Map<String, Double> sizePrices) { this.sizePrices = sizePrices; }
}
