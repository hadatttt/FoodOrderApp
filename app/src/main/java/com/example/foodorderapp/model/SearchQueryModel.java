package com.example.foodorderapp.model;

import java.util.Date;

public class SearchQueryModel {
    private String userId;
    private String keyword;
    private Date timestamp;

    public SearchQueryModel() {
    }


    public SearchQueryModel(String userId, String keyword, Date timestamp) {
        this.userId = userId;
        this.keyword = keyword;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
