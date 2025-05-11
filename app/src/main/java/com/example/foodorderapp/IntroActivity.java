package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.ui.LoginActivity;

import java.util.Arrays;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ImageView[] dots;
    private int currentIndex = 0;
    private Button btnNext, btnCancel;
    private ImageView ivOrder;
    private TextView tv;
    private int[] imageResources;


    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);
        imageResources = new int[]{
                R.drawable.order,
                R.drawable.chef,
                R.drawable.delivery
        };

        tv = findViewById(R.id.textView);

        ivOrder = findViewById(R.id.iv_order);
        dots = new ImageView[] {
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3)
        };
        updateDots();

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            currentIndex = currentIndex + 1;
            if (currentIndex == 0) {
                tv.setText("Tất cả sở thích của bạn");
            } else if (currentIndex == 1) {
                tv.setText("Đặt món đến từ đầu bếp");
            } else if (currentIndex == 2) {
                tv.setText("Miễn phí vận chuyển");
            }
            if (currentIndex<3) {
                updateDots();
            } else {
                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            ivOrder.setImageResource(imageResources[currentIndex]);
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == currentIndex);
        }
    }
}
