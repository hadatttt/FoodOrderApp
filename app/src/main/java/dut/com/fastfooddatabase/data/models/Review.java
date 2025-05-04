package dut.com.fastfooddatabase.data.models;

import java.util.Date;

public class Review {
    private String id;
    private String userId;
    private String restaurantId;
    private double rating;
    private String comment;
    private Date createdAt;

    public Review() {
        // Default constructor required for calls to DataSnapshot.getValue(Review.class)
    }

    public Review(String id, String userId, String restaurantId, double rating, String comment, Date createdAt) {
        this.id = id;
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.rating = rating;
        this.comment = comment;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
