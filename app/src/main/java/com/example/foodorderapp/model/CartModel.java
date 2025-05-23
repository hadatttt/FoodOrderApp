package com.example.foodorderapp.model;

public class CartModel {
    private int foodId;
    private String size;
    private int quantity;
    private double price;
    private String userId;

    public CartModel() {}

    public CartModel(int foodId, String size, int quantity, double price, String userId) {
        this.foodId = foodId;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.userId = userId;
    }

    // Getter & Setter
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}