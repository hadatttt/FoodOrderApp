package com.example.foodorderapp.service;

import android.util.Log;

import com.example.foodorderapp.model.FavouriteModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.ArrayList;

public class FavouriteService {
    private FirebaseFirestore db;

    public FavouriteService() {
        db = FirebaseFirestore.getInstance();
    }

    // Lấy tài liệu Favourite theo userId (String)
    private Task<DocumentSnapshot> getFavouriteDocByUserId(String userId) {
        TaskCompletionSource<DocumentSnapshot> taskSource = new TaskCompletionSource<>();

        db.collection("favourites")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        taskSource.setResult(querySnapshot.getDocuments().get(0));
                    } else {
                        taskSource.setResult(null);
                    }
                })
                .addOnFailureListener(taskSource::setException);

        return taskSource.getTask();
    }

    // Thêm món ăn vào danh sách yêu thích
    public Task<Void> addFoodToFavourite(String userId, int foodId) {
        return getFavouriteDocByUserId(userId)
                .continueWithTask(task -> {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        FavouriteModel model = doc.toObject(FavouriteModel.class);
                        ArrayList<Integer> currentList = model.getListFood();
                        if (!currentList.contains(foodId)) {
                            currentList.add(foodId);
                            return db.collection("favourites").document(doc.getId()).update("listFood", currentList);
                        } else {
                            return Tasks.forResult(null); // Món đã có, không thêm lại
                        }
                    } else {
                        // Chưa có favourite, tạo mới
                        ArrayList<Integer> newList = new ArrayList<>();
                        newList.add(foodId);
                        FavouriteModel newModel = new FavouriteModel(userId, newList);
                        return db.collection("favourites").add(newModel).continueWithTask(addTask -> Tasks.forResult(null));
                    }
                });
    }

    // Xóa món khỏi danh sách yêu thích
    public Task<Void> removeFoodFromFavourite(String userId, int foodId) {
        return getFavouriteDocByUserId(userId)
                .continueWithTask(task -> {
                    DocumentSnapshot doc = task.getResult();
                    if (doc != null && doc.exists()) {
                        FavouriteModel model = doc.toObject(FavouriteModel.class);
                        ArrayList<Integer> currentList = model.getListFood();
                        currentList.remove(Integer.valueOf(foodId));
                        return db.collection("favourites").document(doc.getId()).update("listFood", currentList);
                    } else {
                        return Tasks.forException(new Exception("Favourite list not found for userId: " + userId));
                    }
                });
    }

    // Kiểm tra món đã có trong danh sách yêu thích chưa
    public Task<Boolean> isFoodFavourite(String userId, int foodId) {
        TaskCompletionSource<Boolean> source = new TaskCompletionSource<>();

        getFavouriteDocByUserId(userId)
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        FavouriteModel model = doc.toObject(FavouriteModel.class);
                        source.setResult(model.getListFood().contains(foodId));
                    } else {
                        source.setResult(false);
                    }
                })
                .addOnFailureListener(source::setException);

        return source.getTask();
    }

    // Lắng nghe thay đổi danh sách yêu thích
    public ListenerRegistration listenToFavourite(String userId, EventListener<QuerySnapshot> listener) {
        return db.collection("favourites")
                .whereEqualTo("userId", userId)
                .addSnapshotListener(listener);
    }

    // Lấy danh sách yêu thích theo userId
    public Task<ArrayList<Integer>> getFavouriteList(String userId) {
        TaskCompletionSource<ArrayList<Integer>> source = new TaskCompletionSource<>();

        getFavouriteDocByUserId(userId)
                .addOnSuccessListener(doc -> {
                    if (doc != null && doc.exists()) {
                        FavouriteModel model = doc.toObject(FavouriteModel.class);
                        source.setResult(model.getListFood());
                    } else {
                        source.setResult(new ArrayList<>());
                    }
                })
                .addOnFailureListener(source::setException);

        return source.getTask();
    }
}
