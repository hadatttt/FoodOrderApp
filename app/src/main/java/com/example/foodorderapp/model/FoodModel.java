package com.example.foodorderapp.model;

public class FoodModel {
    private int foodId;
    private int storeId;
    private String name;
    private double price;
    private float rating;
    private int sold;
    private String category;
    private String imageUrl;
    private String caption;      // Mô tả ngắn
    private boolean canUpsize;   // Có thể tăng size hay không
    private double upsizePrice;  // Giá tiền để tăng size

    // Constructor mặc định (Firestore yêu cầu)
    public FoodModel() {
        // Required empty constructor for Firestore
    }

    // Constructor đầy đủ
    public FoodModel(int foodId, int storeId, String name, double price, float rating,
                     int sold, String category, String imageUrl, String caption,
                     boolean canUpsize, double upsizePrice) {
        this.foodId = foodId;
        this.storeId = storeId;
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.sold = sold;
        this.category = category;
        this.imageUrl = imageUrl;
        this.caption = caption;
        this.canUpsize = canUpsize;
        this.upsizePrice = upsizePrice;
    }

    // Getter và Setter
    public int getFoodId() { return foodId; }
    public void setFoodId(int foodId) { this.foodId = foodId; }

    public int getStoreId() { return storeId; }
    public void setStoreId(int storeId) { this.storeId = storeId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getSold() { return sold; }
    public void setSold(int sold) { this.sold = sold; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCaption() { return caption; }
    public void setCaption(String caption) { this.caption = caption; }

    public boolean isCanUpsize() { return canUpsize; }
    public void setCanUpsize(boolean canUpsize) { this.canUpsize = canUpsize; }

    public double getUpsizePrice() { return upsizePrice; }
    public void setUpsizePrice(double upsizePrice) { this.upsizePrice = upsizePrice; }
}
