package com.example.foodorderapp.model;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class FavouriteModel {
    private String userId;
    private ArrayList<Integer> listFood;

    public FavouriteModel() {
    }

    public FavouriteModel(String userId, ArrayList<Integer> listFood) {
        this.userId = userId;
        this.listFood = listFood;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public ArrayList<Integer> getListFood() {
        return listFood;
    }

    public void setListFood(ArrayList<Integer> listFood) {
        this.listFood = listFood;
    }
}
