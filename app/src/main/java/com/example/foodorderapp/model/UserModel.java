package com.example.foodorderapp.model;

public class UserModel {
    private String uid;
    private String firstName;
    private String lastName;
    private String address;

    public UserModel() {
        // Required for Firestore
    }

    public UserModel(String uid, String firstName, String lastName, String address) {
        this.uid = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}