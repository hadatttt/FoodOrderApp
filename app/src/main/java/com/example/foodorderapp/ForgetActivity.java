package com.example.foodorderapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edtEmail;
    private Button btnRecovery;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        // Ánh xạ view
        btnBack = findViewById(R.id.btn_back);
        edtEmail = findViewById(R.id.edt_email);
        btnRecovery = findViewById(R.id.btn_recovery);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Xử lý nút khôi phục mật khẩu
        btnRecovery.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            if (email.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập địa chỉ email", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Đã gửi email khôi phục mật khẩu", Toast.LENGTH_LONG).show();
                            finish(); // quay lại màn hình trước
                        } else {
                            Toast.makeText(this, "Lỗi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
