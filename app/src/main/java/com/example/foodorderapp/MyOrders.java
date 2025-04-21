package com.example.foodorderapp;

public class MyOrders {
    private int id;
    private String title;
    private String image;
    private String name;
    private String price;
    private int quantity;
    private String status;
    private String date;

    public MyOrders(int id, String tile, String image, String name, String price, int quantity, String status, String date) {
        this.id = id;
        this.title = tile;
        this.quantity = quantity;
        this.price = price;
        this.name = name;
        this.image = image;
        this.status = status;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
