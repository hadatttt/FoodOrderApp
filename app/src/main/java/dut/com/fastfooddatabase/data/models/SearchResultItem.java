package dut.com.fastfooddatabase.data.models;

public class SearchResultItem {
    private String name;
    private String imageUrl;
    private double rating;

    // Constructor
    public SearchResultItem(String name, String imageUrl, double rating) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.rating = rating;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

}
