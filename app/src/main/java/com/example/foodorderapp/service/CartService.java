package com.example.foodorderapp.service;

import com.example.foodorderapp.model.CartModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.*;

import java.util.HashMap;
import java.util.Map;

public class CartService {
    private final FirebaseFirestore db;
    private final CollectionReference cartCollection;

    public CartService(String userId) {
        db = FirebaseFirestore.getInstance();
        // Tách giỏ hàng theo người dùng
        cartCollection = db.collection("carts").document(userId).collection("items");
    }

    // Lấy toàn bộ item trong giỏ hàng
    public Task<QuerySnapshot> getAllCartItems() {
        return cartCollection.get();
    }

    // Thêm item vào giỏ hàng
    public Task<DocumentReference> addCartItem(CartModel cartItem) {
        return cartCollection.add(convertCartToMap(cartItem));
    }

    // Cập nhật item trong giỏ hàng (tìm theo foodId)
    public Task<Void> updateCartItem(int foodId, CartModel updatedItem) {
        return cartCollection.whereEqualTo("foodId", foodId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                        return docRef.update(convertCartToMap(updatedItem));
                    }
                    return Tasks.forException(new Exception("Cart item not found"));
                });
    }

    // Xóa item khỏi giỏ hàng
    public Task<Void> deleteCartItem(int foodId) {
        return cartCollection.whereEqualTo("foodId", foodId).get()
                .continueWithTask(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        DocumentReference docRef = task.getResult().getDocuments().get(0).getReference();
                        return docRef.delete();
                    }
                    return Tasks.forException(new Exception("Cart item not found"));
                });
    }

    private Map<String, Object> convertCartToMap(CartModel cart) {
        Map<String, Object> data = new HashMap<>();
        data.put("foodId", cart.getFoodId());
        data.put("size", cart.getSize());
        data.put("quantity", cart.getQuantity());
        data.put("price", cart.getPrice());
        return data;
    }
}
