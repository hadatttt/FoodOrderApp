package com.example.foodorderapp.service;

import android.util.Log;

import com.example.foodorderapp.model.UserModel;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
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

    public String getUserId() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            return currentUser.getUid();
        } else {
            return null;
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

    public Task<FirebaseUser> loginUser(String email, String password) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        // Thực hiện đăng nhập bằng email và password
        return mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            Log.d("UserService", "Đăng nhập thành công: " + user.getEmail());
                        }
                    } else {
                        Log.e("UserService", "Đăng nhập thất bại", task.getException());
                        throw new RuntimeException("Login failed: " + task.getException().getMessage());
                    }
                }).continueWith(task -> {
                    if (task.isSuccessful()) {
                        return mAuth.getCurrentUser();
                    } else {
                        return null;
                    }
                });
    }

    public Task<Void> registerUser(String email, String password, UserModel userModel) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        return mAuth.createUserWithEmailAndPassword(email, password)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            return addUser(userModel);
                        } else {
                            return Tasks.forException(new Exception("Tài khoản đã tạo nhưng không tìm thấy người dùng."));
                        }
                    } else {
                        Exception exception = task.getException();
                        String errorMessage = "Đã xảy ra lỗi khi đăng ký.";

                        if (exception != null) {
                            String message = exception.getMessage();

                            if (message != null) {
                                if (message.contains("The email address is already in use")) {
                                    errorMessage = "Địa chỉ email đã được sử dụng.";
                                } else if (message.contains("The email address is badly formatted")) {
                                    errorMessage = "Địa chỉ email không đúng định dạng.";
                                } else if (message.contains("The given password is invalid") || message.contains("Password should be at least")) {
                                    errorMessage = "Mật khẩu không hợp lệ. Mật khẩu nên có ít nhất 6 ký tự.";
                                } else if (message.contains("network error")) {
                                    errorMessage = "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối Internet.";
                                }
                            }
                        }

                        return Tasks.forException(new Exception(errorMessage));
                    }
                });
    }


    public Task<Void> loginWithGoogle(String idToken) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        return mAuth.signInWithCredential(credential)
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        String email = user.getEmail();
                        UserModel userModel = new UserModel();
                        userModel.setFullName(email.split("@")[0]);
                        userModel.setPhone("");
                        userModel.setAddress("");

                        // Kiểm tra người dùng đã tồn tại (lấy theo UID)
                        return getUser()
                                .continueWithTask(getUserTask -> {
                                    if (!getUserTask.getResult().exists()) {
                                        // Người dùng chưa tồn tại => thêm
                                        return addUser(userModel);
                                    } else {
                                        return Tasks.forResult(null); // Người dùng đã tồn tại, không cần thêm mới
                                    }
                                });
                    } else {
                        throw task.getException();
                    }
                });
    }


}
