package com.example.foodorderapp.model;

import java.util.List;

public class OrderModel {
    //id
    //card id
    //datetime
    //trang thai
    private String orderId;
    private String userId;

    private int foodId;
    private int quantity;
    private double price;

    private String orderDate;
    private String status;
    private String size;

    public OrderModel() {
    }

    public OrderModel(String userId, int foodId, int quantity, double price, String size, String orderDate, String status) {
        this.userId = userId;
        this.foodId = foodId;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
        this.size = size;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getFoodId() {
        return foodId;
    }

    public void setFoodId(int foodId) {
        this.foodId = foodId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }
}
