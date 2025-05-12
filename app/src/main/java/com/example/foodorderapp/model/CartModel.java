package com.example.foodorderapp.model;

public class CartModel {
    private int foodId;
    private String size;
    private int quantity;
    private String userId;
    private int price;

    public CartModel(int foodId, String size, int quantity, String userId, int price) {
        this.foodId = foodId;
        this.size = size;
        this.quantity = quantity;
        this.userId = userId;
        this.price = price;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

}
