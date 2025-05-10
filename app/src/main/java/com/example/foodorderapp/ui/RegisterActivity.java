package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtName, edtEmail, edtPhone, edtPassword;
    private Button btnRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private TextView tvRegisterError;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edt_name);
        edtEmail = findViewById(R.id.edt_email);
        edtPhone = findViewById(R.id.edt_phonenumber);
        edtPassword = findViewById(R.id.edt_password);
        btnRegister = findViewById(R.id.btn_register);
        tvRegisterError = findViewById(R.id.tv_register_error);
        tvRegisterError.setVisibility(View.GONE);  // Ẩn khi chưa có lỗi

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String phone = edtPhone.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty() || name.isEmpty() || phone.isEmpty()) {
                    tvRegisterError.setText("Vui lòng điền đầy đủ thông tin");
                    tvRegisterError.setVisibility(View.VISIBLE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String userId = mAuth.getCurrentUser().getUid();

                                // Thêm thông tin người dùng vào Firestore
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("fullName", name);
                                userMap.put("e-mail", email);
                                userMap.put("phone", phone);

                                db.collection("user").document(userId)  // Dùng userId làm document ID
                                        .set(userMap)
                                        .addOnSuccessListener(aVoid -> {
                                            tvRegisterError.setVisibility(View.GONE);
                                            startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                                            finish();
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(RegisterActivity.this, "Lỗi lưu Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                            } else {
                                // Xử lý lỗi đăng ký
                                String errorMessage = task.getException().getMessage();
                                if (errorMessage != null) {
                                    if (errorMessage.contains("The email address is already in use")) {
                                        tvRegisterError.setText("Email này đã được đăng ký.");
                                    } else if (errorMessage.contains("The password is invalid")) {
                                        tvRegisterError.setText("Mật khẩu không hợp lệ.");
                                    } else if (errorMessage.contains("The email address is badly formatted")) {
                                        tvRegisterError.setText("Email không đúng định dạng.");
                                    }
                                    tvRegisterError.setVisibility(View.VISIBLE);
                                }
                            }
                        });
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ Firebase > Project settings
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleLogin = findViewById(R.id.btn_login_google);
        googleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(RegisterActivity.this, HomeActivity.class));
                        finish();
                    } else {
                        Toast.makeText(this, "Google đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
