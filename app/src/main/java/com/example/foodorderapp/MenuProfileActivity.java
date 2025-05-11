package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.databinding.ActivityMenuProfileBinding;

public class MenuProfileActivity extends AppCompatActivity {
    public ActivityMenuProfileBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = com.example.foodorderapp.databinding.ActivityMenuProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfileActivity.this, EditInfoActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        // TODO: xử lý đăng xuất ở đây
                        Toast.makeText(this, "Đã đăng xuất", Toast.LENGTH_SHORT).show();
                        // Ví dụ: chuyển về màn hình đăng nhập
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish(); // kết thúc activity hiện tại
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
}