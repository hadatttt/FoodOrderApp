package com.example.foodorderapp;

public class ShopModel {
    private int id;
    private String shopName;
    private String address;
    private String discount;
    private int imageResource;

    // Constructor
    public ShopModel(int id, String shopName, String address, String discount, int imageResource) {
        this.id = id;
        this.shopName = shopName;
        this.address = address;
        this.discount = discount;
        this.imageResource = imageResource;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getShopName() {
        return shopName;
    }

    public String getAddress() {
        return address;
    }

    public String getDiscount() {
        return discount;
    }

    public int getImageResource() {
        return imageResource;
    }
}