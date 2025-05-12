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
        return orderCollection.orderBy("orderId", Query.Direction.DESCENDING).limit(1).get()
                .continueWithTask(task -> {
                    int newOrderId = 1;
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentSnapshot lastOrder = task.getResult().getDocuments().get(0);
                        Long lastId = lastOrder.getLong("orderId"); // Firestore lưu số dưới dạng Long
                        if (lastId != null) {
                            newOrderId = lastId.intValue() + 1;
                        }
                    }
                    order.setOrderId(newOrderId);
                    return orderCollection.document(String.valueOf(newOrderId)).set(convertOrderToMap(order));
                });
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
        data.put("foodId", order.getFoodId());
        data.put("quantity", order.getQuantity());
        data.put("price", order.getPrice());
        return data;
    }
}
