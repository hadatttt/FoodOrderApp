package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class PasswordActivity extends AppCompatActivity {
    private UserService userService;
    private UserModel userModel;
    private EditText editCurrentPassword;
    private EditText editNewPassword;
    private EditText editConfirmPassword;
    private ImageButton btnBack;
    private TextView tvError;
    private Button btnSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_password);
        init();
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changePass();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void init(){
        userService = new UserService();
        editCurrentPassword = findViewById(R.id.editCurrentPassword);
        editNewPassword = findViewById(R.id.editNewPassword);
        editConfirmPassword = findViewById(R.id.editConfirmPassword);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);
        tvError = findViewById(R.id.tvError);
    }
    public void changePass(){
        String email = getIntent().getStringExtra("user_email");
        String currentPassword = editCurrentPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();

        AuthCredential credential = EmailAuthProvider.getCredential(email, currentPassword);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.reauthenticate(credential).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                user.updatePassword(newPassword).addOnCompleteListener(updateTask -> {
                    if (updateTask.isSuccessful()) {
                        Toast.makeText(this, "Đổi mật khẩu thành công", Toast.LENGTH_SHORT).show();
                        tvError.setVisibility(View.GONE);
                    } else {
                        tvError.setText("Không thể đổi mật khẩu. Thử lại.");
                        tvError.setVisibility(View.VISIBLE);
                    }
                });
            } else {
                tvError.setText("Sai mật khẩu hiện tại!");
                tvError.setVisibility(View.VISIBLE);
            }
        });
    }
}