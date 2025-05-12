package com.example.foodorderapp.service;

import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.OrderModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.*;

public class OrderService {
    private final FirebaseFirestore db;
    private final CollectionReference orderCollection;

    public OrderService() {
        db = FirebaseFirestore.getInstance();
        orderCollection = db.collection("orders");
    }

    // Lấy tất cả đơn hàng của 1 người dùng
    public Task<QuerySnapshot> getOrdersByUserId(String userId) {
        return orderCollection.whereEqualTo("userId", userId).get();
    }

    // Lấy toàn bộ đơn hàng
    public Task<QuerySnapshot> getAllOrders() {
        return orderCollection.get();
    }

    // Lấy đơn hàng theo orderId
    public Task<DocumentSnapshot> getOrderById(String orderId) {
        return orderCollection.document(orderId).get();
    }

    // Thêm đơn hàng mới
    public Task<Void> addOrder(OrderModel order) {
        String orderId = UUID.randomUUID().toString();
        order.setOrderId(orderId);
        return orderCollection.document(orderId).set(convertOrderToMap(order));
    }

    // Cập nhật đơn hàng theo orderId
    public Task<Void> updateOrder(String orderId, OrderModel order) {
        return orderCollection.document(orderId).update(convertOrderToMap(order));
    }

    // Xoá đơn hàng
    public Task<Void> deleteOrder(String orderId) {
        return orderCollection.document(orderId).delete();
    }

    // Chuyển đổi OrderModel sang Map để lưu vào Firestore
    private Map<String, Object> convertOrderToMap(OrderModel order) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("userId", order.getUserId());
        data.put("orderDate", order.getOrderDate());
        data.put("status", order.getStatus());

        // Chuyển List<CartModel> sang List<Map<String, Object>>
        List<Map<String, Object>> cartMaps = new ArrayList<>();
        for (CartModel cart : order.getCartItems()) {
            Map<String, Object> cartMap = new HashMap<>();
            cartMap.put("foodId", cart.getFoodId());
            cartMap.put("name", cart.getName());
            cartMap.put("imageUrl", cart.getImageUrl());
            cartMap.put("price", cart.getPrice());
            cartMap.put("quantity", cart.getQuantity());
            cartMaps.add(cartMap);
        }

        data.put("cartItems", cartMaps);
        return data;
    }
}
