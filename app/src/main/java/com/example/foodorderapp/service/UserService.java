package com.example.foodorderapp.service;
import com.example.foodorderapp.model.UserModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
public class UserService {
    private FirebaseFirestore db;

    public UserService() {
        db = FirebaseFirestore.getInstance();
    }

    // Thêm người dùng mới vào Firestore với ID là UID từ Firebase Authentication
    public Task<Void> addUser(UserModel user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);
            return userRef.set(user);
        } else {
            // Xử lý khi người dùng chưa đăng nhập
            return Tasks.forException(new Exception("User not logged in"));
        }
    }

    // Lấy thông tin người dùng từ Firestore bằng UID
    public Task<DocumentSnapshot> getUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);
            return userRef.get();
        } else {
            // Xử lý khi người dùng chưa đăng nhập
            return Tasks.forException(new Exception("User not logged in"));
        }
    }

    // Cập nhật thông tin người dùng trong Firestore
    public Task<Void> updateUser(UserModel user) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);
            return userRef.set(user);
        } else {
            // Xử lý khi người dùng chưa đăng nhập
            return Tasks.forException(new Exception("User not logged in"));
        }
    }

    // Xóa người dùng khỏi Firestore
    public Task<Void> deleteUser() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            DocumentReference userRef = db.collection("users").document(uid);
            return userRef.delete();
        } else {
            // Xử lý khi người dùng chưa đăng nhập
            return Tasks.forException(new Exception("User not logged in"));
        }
    }
}
