package com.example.foodorderapp.model;

public class OrderModel {
    private int orderId;
    private int cartId;
    private String datetime;
    private String status;

    public OrderModel(int orderId, int cartId, String datetime, String status) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.datetime = datetime;
        this.status = status;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCartId() {
        return cartId;
    }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
