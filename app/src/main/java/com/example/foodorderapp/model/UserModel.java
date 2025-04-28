package com.example.foodorderapp.model;

public class UserModel {
    private String uid;
    private String name;
    private String address;

    public UserModel() {
        // Required for Firestore
    }

    public UserModel(String uid, String name, String address) {
        this.uid = uid;
        this.name = name;
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
