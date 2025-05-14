package com.example.foodorderapp.model;

public class ToppingModel {
    private String name;
    private double price;
    private String imageUrl;


    public ToppingModel() {
        // Required for Firestore
    }

    public ToppingModel(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
}
