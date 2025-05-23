package com.example.foodorderapp.service;

import com.example.foodorderapp.model.OrderModel;
import com.google.android.gms.tasks.Task;
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
        return orderCollection
                .whereEqualTo("userId", userId)
                .get(Source.SERVER);
    }

    // Lấy toàn bộ đơn hàng
    public Task<QuerySnapshot> getAllOrders() {
        return orderCollection.get();
    }

    // Lấy đơn hàng theo orderId
    public Task<QuerySnapshot> getOrderById(String orderId) {
        return orderCollection.whereEqualTo("orderId", orderId).get();
    }

    // Thêm đơn hàng mới
    public Task<Void> addOrder(OrderModel order) {
        String documentId = orderCollection.document().getId();

        order.setOrderId(documentId);
        return orderCollection.document(documentId).set(convertOrderToMap(order));
    }

    // Cập nhật đơn hàng theo orderId
    public Task<Void> updateOrder(String orderId, OrderModel order) {
        // Tìm đơn hàng với orderId và cập nhật
        return orderCollection.whereEqualTo("orderId", orderId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        return orderCollection.document(documentSnapshot.getId()).update(convertOrderToMap(order));
                    } else {
                        throw new Exception("Order not found");
                    }
                });
    }

    // Xoá đơn hàng
    public Task<Void> deleteOrder(String orderId) {
        return orderCollection.whereEqualTo("orderId", orderId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        return orderCollection.document(documentSnapshot.getId()).delete();
                    } else {
                        throw new Exception("Order not found");
                    }
                });
    }

    // Chuyển đổi OrderModel sang Map để lưu vào Firestore
    private Map<String, Object> convertOrderToMap(OrderModel order) {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", order.getOrderId());
        data.put("userId", order.getUserId());
        data.put("orderDate", order.getOrderDate());
        data.put("status", order.getStatus());
        data.put("foodId", order.getFoodId());
        data.put("quantity", order.getQuantity());
        data.put("size", order.getSize());
        data.put("price", order.getPrice());
        return data;
    }
    public Task<QuerySnapshot> getOrdersByFoodIds(List<Integer> foodIds) {
        return db.collection("orders")
                .whereIn("foodId", foodIds)
                .get();
    }

}