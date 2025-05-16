package com.example.foodorderapp.model;
public class SearchResultModel {
    private String type;
    private FoodModel food;
    private ShopModel shop;

    public SearchResultModel(String type, FoodModel food, ShopModel shop) {
        this.type = type;
        this.food = food;
        this.shop = shop;
    }

    public String getType() {
        return type;
    }

    public FoodModel getFood() {
        return food;
    }

    public ShopModel getShop() {
        return shop;
    }
}
