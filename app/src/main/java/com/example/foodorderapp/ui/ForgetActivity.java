package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ForgetActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private EditText edtEmail;
    private Button btnRecovery;
    private FirebaseAuth mAuth;
    private ProgressBar progressBar;
    private TextView tvForgetError;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        // Ánh xạ view
        btnBack = findViewById(R.id.btn_back);
        edtEmail = findViewById(R.id.edt_email);
        btnRecovery = findViewById(R.id.btn_recovery);

        tvForgetError = findViewById(R.id.tv_forget_error);

        // Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Xử lý nút quay lại
        btnBack.setOnClickListener(v -> onBackPressed());

        // Xử lý nút khôi phục mật khẩu
        btnRecovery.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();

            progressBar.setVisibility(View.VISIBLE);
            if (email.isEmpty()) {
                progressBar.setVisibility(View.GONE);
                tvForgetError.setVisibility(View.VISIBLE);
                tvForgetError.setText("Vui lòng nhập địa chỉ email");
                return;
            }

            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            tvForgetError.setVisibility(View.GONE);
                            Intent intent = new Intent(ForgetActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            tvForgetError.setVisibility(View.VISIBLE);
                            tvForgetError.setText("Email không hợp lệ");
                            progressBar.setVisibility(View.GONE);
                        }
                    });
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
