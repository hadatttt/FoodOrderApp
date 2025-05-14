package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.databinding.ActivityIntroBinding;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.HomeActivity;
import com.example.foodorderapp.ui.HomeActivity1;
import com.example.foodorderapp.ui.LoginActivity;
import com.example.foodorderapp.ui.RegisterActivity1;

public class IntroActivity extends AppCompatActivity {

    private ActivityIntroBinding binding;
    private int[] imageResources;
    private int currentIndex = 0;
    private ImageView[] dots;

    private final UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityIntroBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.progressBar.setVisibility(View.VISIBLE); // hiển thị tiến trình trong lúc load

        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    binding.progressBar.setVisibility(View.GONE);

                    // Nếu chưa có user => load intro
                    if (!documentSnapshot.exists()) {
                        loadComponents();
                        return;
                    }

                    UserModel userModel = documentSnapshot.toObject(UserModel.class);
                    if (userModel == null) {
                        loadComponents();
                        return;
                    }

                    if (userModel.getFullName() == null || userModel.getFullName().isEmpty()) {
                        startActivity(new Intent(IntroActivity.this, RegisterActivity1.class));
                        finish();
                        return;
                    }

                    startActivity(new Intent(IntroActivity.this, HomeActivity1.class));
                    finish();
                })
                .addOnFailureListener(e -> {
                    binding.progressBar.setVisibility(View.GONE);
                    loadComponents();
                });
    }

    private void loadComponents() {
        imageResources = new int[]{
                R.drawable.order,
                R.drawable.chef,
                R.drawable.delivery
        };

        dots = new ImageView[]{
                binding.dot1,
                binding.dot2,
                binding.dot3
        };

        binding.ivOrder.setImageResource(imageResources[currentIndex]);
        binding.textView.setText("Tất cả sở thích của bạn");
        updateDots();

        binding.btnNext.setOnClickListener(v -> {
            currentIndex++;
            if (currentIndex < imageResources.length) {
                binding.ivOrder.setImageResource(imageResources[currentIndex]);
                switch (currentIndex) {
                    case 0:
                        binding.textView.setText("Tất cả sở thích của bạn");
                        break;
                    case 1:
                        binding.textView.setText("Đặt món đến từ đầu bếp");
                        break;
                    case 2:
                        binding.textView.setText("Miễn phí vận chuyển");
                        break;
                }
                updateDots();
            } else {
                binding.progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(IntroActivity.this, LoginActivity.class));
                finish();
            }
        });

        binding.btnCancel.setOnClickListener(v -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            startActivity(new Intent(IntroActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == currentIndex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.progressBar.setVisibility(View.GONE);
    }
}
