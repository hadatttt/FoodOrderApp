package com.example.foodorderapp.service;

import com.example.foodorderapp.model.ShopModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.DocumentReference;

public class ShopService {
    private FirebaseFirestore db;

    public ShopService() {
        db = FirebaseFirestore.getInstance();
    }


    // Thêm cửa hàng vào Firestore
    public Task<DocumentReference> addShop(ShopModel shopModel) {
        CollectionReference shopCollection = db.collection("shops");
        return shopCollection.add(shopModel); // Trả về DocumentReference sau khi thêm cửa hàng vào Firestore
    }

    // Lấy tất cả cửa hàng
    public Task<QuerySnapshot> getAllShops() {
        CollectionReference shopCollection = db.collection("shops");
        return shopCollection.get();  // Lấy tất cả cửa hàng từ Firestore
    }

    // Lấy cửa hàng theo storeId
    public Task<QuerySnapshot> getShopById(int storeId) {
        CollectionReference shopCollection = db.collection("shops");
        return shopCollection.whereEqualTo("storeid", storeId).get();  // Truy vấn cửa hàng theo storeId
    }

    // Cập nhật cửa hàng
    public Task<QuerySnapshot> updateShop(int storeId, ShopModel shopModel) {
        CollectionReference shopCollection = db.collection("shops");
        return shopCollection.whereEqualTo("storeid", storeId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Chỉ lấy một tài liệu (cửa hàng) với storeId
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        shopCollection.document(documentSnapshot.getId()).set(shopModel);  // Cập nhật cửa hàng
                    }
                });
    }

    // Xóa cửa hàng
    public Task<QuerySnapshot> deleteShop(int storeId) {
        CollectionReference shopCollection = db.collection("shops");
        return shopCollection.whereEqualTo("storeid", storeId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Chỉ lấy một tài liệu (cửa hàng) với storeId
                        DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        shopCollection.document(documentSnapshot.getId()).delete();  // Xóa cửa hàng
                    }
                });
    }
    public void updateStoreRating(int storeId) {
        CollectionReference foodCollection = db.collection("foods");
        // Lấy tất cả các món ăn của cửa hàng từ collection "foods"
        foodCollection.whereEqualTo("storeid", storeId).get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        double totalRating = 0;
                        int itemCount = queryDocumentSnapshots.size();

                        // Tính tổng số sao của tất cả các món ăn
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Double rating = documentSnapshot.getDouble("rating");  // Lấy số sao từ mỗi món ăn
                            if (rating != null) {
                                totalRating += rating;
                            }
                        }

                        // Tính điểm sao trung bình của cửa hàng
                        double averageRating = (itemCount > 0) ? totalRating / itemCount : 0;

                        // Cập nhật lại điểm sao của cửa hàng
                        CollectionReference shopCollection = db.collection("shops");
                        shopCollection.whereEqualTo("storeid", storeId).get()
                                .addOnSuccessListener(shopQuerySnapshot -> {
                                    if (!shopQuerySnapshot.isEmpty()) {
                                        DocumentSnapshot shopDocument = shopQuerySnapshot.getDocuments().get(0);
                                        shopCollection.document(shopDocument.getId())
                                                .update("rating", averageRating)  // Cập nhật điểm sao trung bình
                                                .addOnSuccessListener(aVoid -> {
                                                    System.out.println("Store rating updated successfully!");
                                                })
                                                .addOnFailureListener(e -> {
                                                    System.out.println("Error updating store rating: " + e.getMessage());
                                                });
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error getting food items: " + e.getMessage());
                });
    }
}
