package com.example.foodorderapp.model;

import java.util.List;

public class ShopModel {
    private int storeid;
    private String shopName;
    private String address;
    private float discount;
    private String imageResource;
    private float rating;
    private String advertisement;

    public ShopModel() {}

    public ShopModel(int storeid, String shopName, String address, float discount, String imageResource, String advertisement) {
        this.storeid = storeid;
        this.shopName = shopName;
        this.address = address;
        this.discount = discount;
        this.imageResource = imageResource;
        this.rating = 0;
        this.advertisement = advertisement;

    }

    // Getters and Setters
    public int getStoreid() { return storeid; }
    public void setStoreid(int storeid) { this.storeid = storeid; }

    public String getShopName() { return shopName; }
    public void setShopName(String shopName) { this.shopName = shopName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public float getDiscount() { return discount; }
    public void setDiscount(float discount) { this.discount = discount; }

    public String getImageResource() { return imageResource; }
    public void setImageResource(String imageResource) { this.imageResource = imageResource; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getAdvertisement() { return advertisement; }
    public void setAdvertisement(String advertisement) { this.advertisement = advertisement; }

}
