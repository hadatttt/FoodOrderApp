package dut.com.fastfooddatabase.data.models;

public class Shop {
    private String id;
    private String name;
    private String address;
    private String phone;
    private double rating;

    private String description;
    private String imageUrl;

    public Shop() {
        // Default constructor required for calls to DataSnapshot.getValue(Shop.class)
    }

    public Shop(String id, String name, String description, String address, String phone, double rating, String imageUrl) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
