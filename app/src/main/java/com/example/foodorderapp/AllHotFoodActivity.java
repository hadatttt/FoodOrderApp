package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.model.FoodModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllHotFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> fullFoodList;
    private List<FoodModel> foodList;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken;
    private List<Button> categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_hot_food);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allhotfood), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Nút quay về
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Quay về mà không khởi động lại HomeActivity
        });

        // Khởi tạo RecyclerView HotFood
        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        // Danh sách món ăn hot
        fullFoodList = new ArrayList<>();
        fullFoodList.add(new FoodModel(1, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger1, 120, "Burger"));
        fullFoodList.add(new FoodModel(2, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza1, 85, "Pizza"));
        fullFoodList.add(new FoodModel(3, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(4, 102, "Mỳ Ý", "110.000đ", 4.1f, R.drawable.spaggetti1, 55, "Spaghetti"));
        foodList = new ArrayList<>(fullFoodList);

        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);

        categoryButtons = Arrays.asList(btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken);

        // Set click listeners
        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));

        // Mặc định chọn All
        selectCategory("Tất cả", btnAll);
    }

    private void selectCategory(String category, Button selectedButton) {
        filterFoods(category);

        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEEEEE)); // xám nhạt
            }
        }
    }

    private void filterFoods(String category) {
        List<FoodModel> filteredFoodList = new ArrayList<>();

        if (category.equals("Tất cả")) {
            filteredFoodList.addAll(fullFoodList);
        } else {
            for (FoodModel food : fullFoodList) {
                if (food.getCategory().equalsIgnoreCase(category)) {
                    filteredFoodList.add(food);
                }
            }
        }

        hotFoodAdapter.updateData(filteredFoodList);
    }
}
