package com.example.foodorderapp.model;

import java.util.List;

public class DistrictModel {
    private int code;
    private String name;
    private List<WardModel> wards;

    public int getCode() { return code; }
    public String getName() { return name; }
    public List<WardModel> getWards() { return wards; }
}
