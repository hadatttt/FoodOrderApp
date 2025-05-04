package dut.com.fastfooddatabase.data.models;

public class OrderItem {
    private String ItemId;
    private int quantity;
    private double price;

    public OrderItem() {

        // Default constructor required for calls to DataSnapshot.getValue(OrderItem.class)
    }

    public OrderItem(String itemId, int quantity, double price) {
        this.ItemId = itemId;
        this.quantity = quantity;
        this.price = price;
    }

    public String getItemId() {
        return ItemId;
    }

    public void setItemId(String itemId) {
        this.ItemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
