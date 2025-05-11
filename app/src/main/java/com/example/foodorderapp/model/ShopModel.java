package com.example.foodorderapp.model;

public class ShopModel {
    private int storeid;
    private String shopName;
    private String address;
    private float discount;
    private String imageUrl;  // Đổi tên biến ở đây
    private float rating;
    private String advertisement;

    public ShopModel() {}

    public ShopModel(int storeid, String shopName, String address, float discount, String imageUrl, String advertisement, float rating) {
        this.storeid = storeid;
        this.shopName = shopName;
        this.address = address;
        this.discount = discount;
        this.imageUrl = imageUrl;
        this.rating = rating;  // Cập nhật constructor để nhận rating
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

    public String getImageUrl() { return imageUrl; }  // Sửa getter
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }  // Sửa setter

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getAdvertisement() { return advertisement; }
    public void setAdvertisement(String advertisement) { this.advertisement = advertisement; }
}
