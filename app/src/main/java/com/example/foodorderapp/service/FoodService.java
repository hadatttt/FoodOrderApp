package com.example.foodorderapp.service;

import com.example.foodorderapp.model.FoodModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class FoodService {
    private FirebaseFirestore db;

    public FoodService() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy thông tin chi tiết thực phẩm từ Firestore
    public Task<QuerySnapshot> getFoodDetails(int foodId) {
        CollectionReference foodCollection = db.collection("foods");
        return foodCollection.whereEqualTo("foodId", foodId).get(); // Trả về Task<QuerySnapshot>
    }

    // Thêm món ăn vào Firestore
    public Task<DocumentReference> addFood(FoodModel food) {
        CollectionReference foodCollection = db.collection("foods");

        // Chuyển đối tượng FoodModel thành Map để lưu vào Firestore
        Map<String, Object> foodData = new HashMap<>();
        foodData.put("foodId", food.getFoodId());
        foodData.put("storeId", food.getStoreId());
        foodData.put("name", food.getName());
        foodData.put("price", food.getPrice());
        foodData.put("rating", food.getRating());
        foodData.put("imageResId", food.getImageResId());
        foodData.put("sold", food.getSold());
        foodData.put("category", food.getCategory());

        return foodCollection.add(foodData); // Thêm món ăn vào Firestore
    }

    // Cập nhật thông tin món ăn trong Firestore
    public Task<Void> updateFood(int foodId, FoodModel food) {
        CollectionReference foodCollection = db.collection("foods");

        // Lấy tài liệu thực phẩm theo foodId
        return foodCollection.whereEqualTo("foodId", foodId).get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Tìm document cần cập nhật
                for (DocumentSnapshot document : task.getResult()) {
                    DocumentReference docRef = document.getReference();

                    // Cập nhật dữ liệu trong tài liệu
                    Map<String, Object> updatedFoodData = new HashMap<>();
                    updatedFoodData.put("name", food.getName());
                    updatedFoodData.put("price", food.getPrice());
                    updatedFoodData.put("rating", food.getRating());
                    updatedFoodData.put("imageResId", food.getImageResId());
                    updatedFoodData.put("sold", food.getSold());
                    updatedFoodData.put("category", food.getCategory());

                    return docRef.update(updatedFoodData); // Cập nhật dữ liệu
                }
            }
            return null; // Nếu không tìm thấy món ăn, trả về null
        });
    }

    // Xóa món ăn khỏi Firestore
    public Task<Void> deleteFood(int foodId) {
        CollectionReference foodCollection = db.collection("foods");

        // Tìm món ăn theo foodId và xóa
        return foodCollection.whereEqualTo("foodId", foodId).get().continueWithTask(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                // Xóa tài liệu của món ăn
                for (DocumentSnapshot document : task.getResult()) {
                    DocumentReference docRef = document.getReference();
                    return docRef.delete(); // Xóa tài liệu
                }
            }
            return null; // Nếu không tìm thấy món ăn, trả về null
        });
    }
}
