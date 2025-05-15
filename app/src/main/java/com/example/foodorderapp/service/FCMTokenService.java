package com.example.foodorderapp.service;

import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

public class FCMTokenService {

    public static void sendTokenToFirestore() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Log.e("FCMTokenService", "User not logged in");
            return;
        }

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.e("FCMTokenService", "Fetching FCM token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();

                    FirebaseFirestore.getInstance()
                            .collection("fcm")
                            .document(userId)
                            .update("fcm", token)
                            .addOnSuccessListener(aVoid -> Log.d("FCMTokenService", "Token updated"))
                            .addOnFailureListener(e -> {
                                // Nếu document chưa tồn tại, tạo mới
                                FirebaseFirestore.getInstance()
                                        .collection("fcm")
                                        .document(userId)
                                        .set(new FCMToken(token))
                                        .addOnSuccessListener(aVoid2 -> Log.d("FCMTokenService", "Token set"))
                                        .addOnFailureListener(e2 -> Log.e("FCMTokenService", "Failed to set token", e2));
                            });
                });
    }

    // Class hỗ trợ lưu token
    public static class FCMToken {
        public String fcm;

        public FCMToken() {
        }

        public FCMToken(String fcm) {
            this.fcm = fcm;
        }
    }
}
