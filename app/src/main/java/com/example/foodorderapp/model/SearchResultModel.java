package com.example.foodorderapp.model;

public class SearchResultModel {
    private String name;
    private Float rating;
    private String imageUrl;
    private int shopId;
    private int foodId;

    public SearchResultModel() {}

    // Getter và Setter
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Float getRating() { return rating; }
    public void setRating(Float rating) { this.rating = rating; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public int getShopId() { return shopId; }
    public void setShopId(int shopId) { this.shopId = shopId; }

    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }
}
