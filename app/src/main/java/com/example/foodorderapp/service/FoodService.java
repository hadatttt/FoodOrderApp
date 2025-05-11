package com.example.foodorderapp.service;

import com.example.foodorderapp.model.FoodModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class FoodService {
    private final FirebaseFirestore db;
    private final CollectionReference foodCollection;

    public FoodService() {
        db = FirebaseFirestore.getInstance();
        foodCollection = db.collection("foods");
    }

    // Lấy toàn bộ danh sách món ăn
    public Task<QuerySnapshot> getAllFoods() {
        return foodCollection.get();
    }

    // Lấy thông tin món ăn theo foodId
    public Task<QuerySnapshot> getFoodDetails(int foodId) {
        return foodCollection.whereEqualTo("foodId", foodId).get();
    }

    // Thêm món ăn mới
    public Task<DocumentReference> addFood(FoodModel food) {
        return foodCollection.add(convertFoodToMap(food));
    }

    // Cập nhật thông tin món ăn theo foodId
    public Task<Void> updateFood(int foodId, FoodModel food) {
        return foodCollection.whereEqualTo("foodId", foodId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                        return docRef.update(convertFoodToMap(food));
                    }
                    return Tasks.forException(new Exception("Food not found"));
                });
    }

    // Xóa món ăn theo foodId
    public Task<Void> deleteFood(int foodId) {
        return foodCollection.whereEqualTo("foodId", foodId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                        return docRef.delete();
                    }
                    return Tasks.forException(new Exception("Food not found"));
                });
    }

    // Chuyển đổi FoodModel sang Map để lưu Firestore
    private Map<String, Object> convertFoodToMap(FoodModel food) {
        Map<String, Object> data = new HashMap<>();
        data.put("foodId", food.getFoodId());
        data.put("storeId", food.getStoreId());
        data.put("name", food.getName());
        data.put("price", food.getPrice());
        data.put("rating", food.getRating());
        data.put("imageResId", food.getImageUrl());
        data.put("sold", food.getSold());
        data.put("category", food.getCategory());
        return data;
    }
}
