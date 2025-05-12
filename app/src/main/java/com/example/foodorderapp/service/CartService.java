package com.example.foodorderapp.service;

import com.example.foodorderapp.model.CartModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

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



    public void deleteCartItemByFoodId(String userId, int foodId) {
        db.collection("carts")
                .whereEqualTo("userId", userId)
                .whereEqualTo("foodId", foodId)
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
