package com.example.foodorderapp.model;

public class SearchResult {
    private String name;
    private String subName; // Tên phụ (tên nhà hàng hoặc loại món ăn)
    private float rating; // -1 if not applicable (e.g., for favorite items)
    private int imageRes;

    public SearchResult(String name, String subName, float rating, int imageRes) {
        this.name = name;
        this.subName = subName;
        this.rating = rating;
        this.imageRes = imageRes;
    }

    public String getName() {
        return name;
    }

    public String getSubName() {
        return subName;
    }

    public float getRating() {
        return rating;
    }

    public int getImageRes() {
        return imageRes;
    }
}