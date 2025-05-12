package com.example.foodorderapp.model;

import java.util.List;

public class OrderModel {
    //id
    //card id
    //datetime
    //trang thai
    private String orderId;
    private String userId;

    private String foodId;
    private String size;
    private int quantity;
    private double price;

    private String orderDate;
    private String status;

    public OrderModel() {
    }

    public OrderModel(String orderId, String userId, String foodId, String size, int quantity, double price, String orderDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.foodId = foodId;
        this.size = size;
        this.quantity = quantity;
        this.price = price;
        this.orderDate = orderDate;
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

    public String getFoodId() {
        return foodId;
    }

    public void setFoodId(String foodId) {
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
