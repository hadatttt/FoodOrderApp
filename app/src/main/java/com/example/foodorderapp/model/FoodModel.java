package com.example.foodorderapp.model;

import java.util.List;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private double price;
    private float rating;
    private int sold;
    private String category;
    private String imageUrl;
//    private List<ToppingModel> toppings; // ✅ Danh sách topping

    public FoodModel() {
        // Required empty constructor for Firestore
    }

    public FoodModel(int foodId, int storeId, String name, double price, float rating,
                     int sold, String category, String imageUrl) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.sold = sold;
        this.category = category;
        this.imageUrl = imageUrl;
//        this.toppings = toppings;
    }

    // Getter và Setter
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

//    public List<ToppingModel> getToppings() { return toppings; }
//    public void setToppings(List<ToppingModel> toppings) { this.toppings = toppings; }
}
