package com.example.foodorderapp.service;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.foodorderapp.model.CartModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CartService {
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference cartRef = db.collection("Cart");

    public void addToCart(CartModel cartItem) {
        // Document ID sẽ là combination giữa userId và foodId để tránh trùng
        String documentId = cartItem.getUserId() + "_" + cartItem.getFoodId();

        Map<String, Object> data = new HashMap<>();
        data.put("foodId", cartItem.getFoodId());
        data.put("size", cartItem.getSize());
        data.put("quantity", cartItem.getQuantity());
        data.put("price", cartItem.getPrice());
        data.put("userId", cartItem.getUserId());

        cartRef.document(documentId)
                .set(data)
                .addOnSuccessListener(unused -> Log.d("CartService", "Item added to cart"))
                .addOnFailureListener(e -> Log.e("CartService", "Failed to add to cart", e));
    }

    public void deleteItemFromCart(String userId, String foodId) {
        String documentId = userId + "_" + foodId;

        cartRef.document(documentId)
                .delete()
                .addOnSuccessListener(unused -> Log.d("CartService", "Item deleted"))
                .addOnFailureListener(e -> Log.e("CartService", "Delete failed", e));
    }

    public void clearCart(String userId) {
        cartRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (var doc : queryDocumentSnapshots) {
                        cartRef.document(doc.getId()).delete();
                    }
                    Log.d("CartService", "Cart cleared");
                })
                .addOnFailureListener(e -> Log.e("CartService", "Failed to clear cart", e));
    }

    public void getCartByUserId(String userId, OnSuccessListener<Iterable<CartModel>> listener, OnFailureListener failureListener) {
        cartRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<CartModel> cartList = new ArrayList<>();
                    for (var doc : queryDocumentSnapshots) {
                        CartModel item = new CartModel(
                                doc.getString("foodId"),
                                doc.getString("size"),
                                doc.getLong("quantity").intValue(),
                                doc.getDouble("price"),
                                doc.getString("userId")
                        );
                        cartList.add(item);
                    }
                    listener.onSuccess(cartList);
                })
                .addOnFailureListener(failureListener);
    }
}
