package com.example.foodorderapp;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> foodList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Handle edge-to-edge display (optional, based on your UI requirements)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize RecyclerView
        recyclerHotFood = findViewById(R.id.recyclerHotFood);

        // Set LayoutManager (uncommented and ensured)
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        // Create sample data
        foodList = new ArrayList<>();
        foodList.add(new FoodModel(1, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger, 120));
        foodList.add(new FoodModel(2, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza, 85));
        foodList.add(new FoodModel(3, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken, 200));

        // Initialize and set adapter
        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);
    }
}