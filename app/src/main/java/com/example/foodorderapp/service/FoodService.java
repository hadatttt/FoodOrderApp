package com.example.foodorderapp.service;

import com.example.foodorderapp.model.FoodModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class FoodService {
    private final FirebaseFirestore db;
    private final CollectionReference foodCollection;

    public FoodService() {
        db = FirebaseFirestore.getInstance();
        foodCollection = db.collection("foods");
    }
    // Lấy danh sách món ăn theo storeId
    public Task<QuerySnapshot> getFoodsByStoreId(int storeId) {
        return foodCollection.whereEqualTo("storeId", storeId).get();
    }

    // Lấy toàn bộ danh sách món ăn
    public Task<QuerySnapshot> getAllFoods() {
        return foodCollection.get();
    }

    // Lấy danh sách món ăn theo tên
    public List<FoodModel> getFoodsByName(String name) {
        try {
            QuerySnapshot snapshot = Tasks.await(foodCollection.get());
            List<FoodModel> foodList = snapshot.toObjects(FoodModel.class);

            if (name == null || name.trim().isEmpty()) {
                return foodList;
            }

            List<FoodModel> results = new ArrayList<>();
            String lowercaseName = name.trim().toLowerCase();
            for (FoodModel food : foodList) {
                String foodName = food.getName().toLowerCase();
                if (foodName.contains(lowercaseName)) {
                    results.add(food);
                }
            }

            return results;
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Lấy thông tin món ăn theo foodId
    public Task<QuerySnapshot> getFoodDetails(int foodId) {
        return foodCollection.whereEqualTo("foodId", foodId).get();
    }

    // Thêm món ăn mới
    public Task<DocumentReference> addFood(FoodModel food) {
        return foodCollection.add(convertFoodToMap(food));
    }

    public Task<QuerySnapshot> getRandomFoods(int limit) {
        return foodCollection.limit(limit).get();
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
        data.put("imageUrl", food.getImageUrl()); // Sử dụng imageUrl thay vì imageResId
        data.put("sold", food.getSold());
        data.put("category", food.getCategory());
        return data;
    }
}
