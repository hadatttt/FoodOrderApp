package com.example.foodorderapp.ui;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FoodService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllHotFoodActivity extends AppCompatActivity {

    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> fullFoodList;
    private List<FoodModel> foodList;
    private FoodService foodService;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken, btnDrink;
    private List<Button> categoryButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_hot_food);

        // Set padding to avoid layout obstruction by system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allhotfood), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize back button to finish the activity
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        // Initialize RecyclerView for Hot Foods
        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this));

        fullFoodList = new ArrayList<>();
        foodList = new ArrayList<>(fullFoodList);
        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        // Category Buttons
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);
        btnDrink = findViewById(R.id.btnDrink);

        categoryButtons = Arrays.asList(btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken, btnDrink);

        // Set category button click listeners
        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));
        btnDrink.setOnClickListener(v -> selectCategory("Drink", btnDrink));

        // Default selection for All category
        selectCategory("Tất cả", btnAll);

        // Initialize FoodService and load data
        foodService = new FoodService();
        loadAllFoods();
    }

    private void selectCategory(String category, Button selectedButton) {
        filterFoods(category);

        for (Button button : categoryButtons) {
            int color = (button == selectedButton) ? 0xFFFFD700 : 0xFFEEEEEE; // gold for selected, light gray for others
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
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

    private void loadAllFoods() {
        foodService.getAllFoods().addOnSuccessListener(querySnapshots -> {
            fullFoodList.clear();
            for (DocumentSnapshot doc : querySnapshots) {
                FoodModel food = new FoodModel(
                        doc.getLong("foodId").intValue(),
                        doc.getLong("storeId").intValue(),
                        doc.getString("name"),
                        doc.getDouble("price"),
                        doc.getDouble("rating").floatValue(),
                        R.drawable.burger1, // Placeholder for image
                        doc.getLong("sold").intValue(),
                        doc.getString("category")
                );
                fullFoodList.add(food);
            }

            // Load all food data into the list and notify adapter
            foodList.clear();
            foodList.addAll(fullFoodList);
            hotFoodAdapter.notifyDataSetChanged();
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading food data", e));
    }
}
