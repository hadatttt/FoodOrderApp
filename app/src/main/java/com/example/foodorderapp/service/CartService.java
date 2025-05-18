package com.example.foodorderapp.service;

import android.util.Log;

import com.example.foodorderapp.model.CartModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.Map;

public class CartService {
    private FirebaseFirestore db;
    private FoodService foodService;


    public CartService() {
        db = FirebaseFirestore.getInstance();
        foodService = new FoodService();
    }

    public Task<DocumentReference> addToCart(CartModel cartModel) {
        return db.collection("carts").add(cartModel);
    }
    public ListenerRegistration listenToCartByUserId(String userId, EventListener<QuerySnapshot> listener) {
        return FirebaseFirestore.getInstance()
                .collection("carts")
                .whereEqualTo("userId", userId)
                .addSnapshotListener(listener);
    }

    public Task<QuerySnapshot> getCartByUserId(String userId) {
        return db.collection("carts").whereEqualTo("userId", userId).get();
    }

    // Cập nhật số lượng giỏ hàng
    public Task<Void> updateCartItem(String userId, int foodId, CartModel updatedModel) {
        // Lấy giá của món ăn từ FoodService
        return foodService.getFoodDetails(foodId)
                .continueWithTask(task -> {
                    if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                        // Lấy giá của món ăn từ Firestore
                        DocumentSnapshot foodSnapshot = task.getResult().getDocuments().get(0);
                        double price = foodSnapshot.getDouble("price");

                        // Tính lại giá trị tổng cho món ăn và cập nhật giỏ hàng
                        updatedModel.setPrice(price * updatedModel.getQuantity());

                        // Lọc giỏ hàng theo userId và foodId
                        return db.collection("carts")
                                .whereEqualTo("userId", userId)  // Lọc theo userId
                                .whereEqualTo("foodId", foodId)  // Lọc theo foodId
                                .get()
                                .continueWithTask(queryTask -> {
                                    if (queryTask.isSuccessful() && !queryTask.getResult().isEmpty()) {
                                        // Lấy documentId của mục giỏ hàng
                                        DocumentSnapshot cartItemSnapshot = queryTask.getResult().getDocuments().get(0);
                                        String cartItemId = cartItemSnapshot.getId();  // Đây là cartItemId

                                        // Cập nhật giỏ hàng trong Firestore
                                        return db.collection("carts").document(cartItemId).set(updatedModel);
                                    } else {
                                        return Tasks.forException(new Exception("Cart item not found"));
                                    }
                                });
                    } else {
                        return Tasks.forException(new Exception("Food not found"));
                    }
                });
    }



    public void deleteCartItemByFoodId(String userId, int foodId, String size) {
        db.collection("carts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("foodId", foodId)
                .whereEqualTo("size", size)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        doc.getReference().delete();  // Xóa từng mục phù hợp
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Xóa giỏ hàng thất bại: " + e.getMessage());
                });
    }
    public Task<Void> checkAndAddOrUpdateCartItem(CartModel newCartItem) {
        TaskCompletionSource<Void> taskSource = new TaskCompletionSource<>();

        db.collection("foods")
                .whereEqualTo("foodId", newCartItem.getFoodId())
                .get()
                .addOnSuccessListener(foodDoc -> {
                    if (!foodDoc.isEmpty()) {
                        DocumentSnapshot foodSnapshot = foodDoc.getDocuments().get(0);

                        // Lấy map sizePrices (nếu có)
                        Map<String, Object> priceBySizeMap = (Map<String, Object>) foodSnapshot.get("sizePrices");

                        double price;

                        // Nếu có sizePrices và chọn size hợp lệ
                        if (priceBySizeMap != null && newCartItem.getSize() != null && !newCartItem.getSize().isEmpty()) {
                            if (priceBySizeMap.containsKey(newCartItem.getSize())) {
                                price = ((Number) priceBySizeMap.get(newCartItem.getSize())).doubleValue();
                            } else {
                                taskSource.setException(new Exception("Không tìm thấy giá theo size"));
                                Log.d("SIZEEEEE", "Không tìm thấy giá theo size: " + newCartItem.getSize());
                                return;
                            }
                        } else {
                            // Không có size → dùng giá mặc định từ trường "price"
                            Number basePrice = foodSnapshot.getDouble("price");
                            if (basePrice != null) {
                                price = basePrice.doubleValue();
                            } else {
                                taskSource.setException(new Exception("Không có trường 'price' trong món ăn"));
                                return;
                            }
                        }

                        newCartItem.setPrice(price);

                        // Kiểm tra xem món này đã có trong giỏ chưa
                        db.collection("carts")
                                .whereEqualTo("userId", newCartItem.getUserId())
                                .whereEqualTo("foodId", newCartItem.getFoodId())
                                .whereEqualTo("size", newCartItem.getSize())
                                .get()
                                .addOnSuccessListener(querySnapshot -> {
                                    if (!querySnapshot.isEmpty()) {
                                        DocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                                        CartModel existingItem = doc.toObject(CartModel.class);

                                        int newQuantity = existingItem.getQuantity() + newCartItem.getQuantity();
                                        existingItem.setQuantity(newQuantity);
                                        existingItem.setPrice(price);

                                        db.collection("carts").document(doc.getId())
                                                .set(existingItem)
                                                .addOnSuccessListener(aVoid -> taskSource.setResult(null))
                                                .addOnFailureListener(taskSource::setException);
                                    } else {
                                        db.collection("carts").add(newCartItem)
                                                .addOnSuccessListener(docRef -> taskSource.setResult(null))
                                                .addOnFailureListener(taskSource::setException);
                                    }
                                })
                                .addOnFailureListener(taskSource::setException);
                    } else {
                        taskSource.setException(new Exception("Không tìm thấy món ăn với ID: " + newCartItem.getFoodId()));
                        Log.d("SIZEEEEE", "Không tìm thấy món ăn với ID");
                    }
                })
                .addOnFailureListener(taskSource::setException);

        return taskSource.getTask();
    }

    public void clearCartByUserId(String userId) {
        db.collection("carts").whereEqualTo("userId", userId).get()
                .addOnSuccessListener(querySnapshots -> {
                    for (DocumentSnapshot doc : querySnapshots.getDocuments()) {
                        db.collection("carts").document(doc.getId()).delete();
                    }
                })
                .addOnFailureListener(e -> {
                    System.out.println("Error clearing cart: " + e.getMessage());
                });
    }
}