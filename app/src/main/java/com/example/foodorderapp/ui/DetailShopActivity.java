package com.example.foodorderapp.ui;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.*;

public class DetailShopActivity extends AppCompatActivity {

    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> fullFoodList = new ArrayList<>();
    private List<FoodModel> foodList = new ArrayList<>();
    private List<Button> categoryButtons = new ArrayList<>();
    private Map<Button, String> buttonCategoryMap = new HashMap<>();

    private final FoodService foodService = new FoodService();
    private final ShopService shopService = new ShopService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_shop);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupUI();
        setupCategoryButtons();

        int storeId = getIntent().getIntExtra("storeId", -1);
        if (storeId == -1) {
            Toast.makeText(this, "Store ID không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadShopInfo(storeId);
        loadFoods(storeId);
    }

    private void setupUI() {
        recyclerHotFood = findViewById(R.id.listFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this));
        hotFoodAdapter = new HotFoodAdapter(this, foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));

        // Gán từng button và category tương ứng
        Button btnAll = findViewById(R.id.btnAll);
        Button btnSpaghetti = findViewById(R.id.btnSpaghetti);
        Button btnPotato = findViewById(R.id.btnPotato);
        Button btnPizza = findViewById(R.id.btnPizza);
        Button btnBurger = findViewById(R.id.btnBurger);
        Button btnChicken = findViewById(R.id.btnChicken);
        Button btnDrink = findViewById(R.id.btnDrink);

        addCategoryButton(btnAll, "tất cả");
        addCategoryButton(btnSpaghetti, "spaghetti");
        addCategoryButton(btnPotato, "potato");
        addCategoryButton(btnPizza, "pizza");
        addCategoryButton(btnBurger, "burger");
        addCategoryButton(btnChicken, "chicken");
        addCategoryButton(btnDrink, "drink");
    }

    private void addCategoryButton(Button button, String category) {
        categoryButtons.add(button);
        buttonCategoryMap.put(button, category);
    }

    private void setupCategoryButtons() {
        for (Button button : categoryButtons) {
            button.setOnClickListener(v -> {
                String category = buttonCategoryMap.get(button);
                if (category != null) {
                    selectCategory(category);
                }
            });
        }
    }

    private void loadShopInfo(int storeId) {
        shopService.getShopById(storeId)
                .addOnSuccessListener(query -> {
                    if (!query.isEmpty()) {
                        DocumentSnapshot doc = query.getDocuments().get(0);
                        setShopInfo(doc);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải thông tin cửa hàng", Toast.LENGTH_SHORT).show());
    }

    private void setShopInfo(DocumentSnapshot doc) {
        ((TextView) findViewById(R.id.tvShopName)).setText(doc.getString("shopName"));
        ((TextView) findViewById(R.id.tvRate)).setText(
                doc.getDouble("rating") != null ? String.format("%.1f", doc.getDouble("rating")) : "N/A");
        ((TextView) findViewById(R.id.tvDesc)).setText(doc.getString("advertisement"));
        String imageUrl = doc.getString("imageUrl");
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this).load(imageUrl).into((ImageView) findViewById(R.id.imgShop));
        }
    }

    private void loadFoods(int storeId) {
        foodService.getFoodsByStoreId(storeId)
                .addOnSuccessListener(query -> {
                    fullFoodList.clear();
                    Set<String> foundCategories = new HashSet<>();

                    for (DocumentSnapshot doc : query) {
                        FoodModel food = doc.toObject(FoodModel.class);
                        if (food != null) {
                            fullFoodList.add(food);
                            if (food.getCategory() != null && !food.getCategory().trim().isEmpty()) {
                                foundCategories.add(food.getCategory().trim().toLowerCase());
                            }
                        }
                    }

                    updateCategoryButtonsVisibility(foundCategories);
                    selectCategory("tất cả");
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi khi tải danh sách món ăn", Toast.LENGTH_SHORT).show());
    }

    private void updateCategoryButtonsVisibility(Set<String> validCategories) {
        for (Button button : categoryButtons) {
            String category = buttonCategoryMap.get(button);
            if (category != null && (category.equals("tất cả") || validCategories.contains(category))) {
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }

    private void selectCategory(String category) {
        List<FoodModel> filtered = new ArrayList<>();
        if (category.equals("tất cả")) {
            filtered.addAll(fullFoodList);
        } else {
            for (FoodModel food : fullFoodList) {
                if (food.getCategory() != null &&
                        food.getCategory().trim().toLowerCase().equals(category)) {
                    filtered.add(food);
                }
            }
        }

        foodList.clear();
        foodList.addAll(filtered);
        hotFoodAdapter.notifyDataSetChanged();
        updateButtonHighlight(category);
    }

    private void updateButtonHighlight(String selectedCategory) {
        for (Button button : categoryButtons) {
            String category = buttonCategoryMap.get(button);
            if (category != null && category.equals(selectedCategory)) {
                button.setBackgroundTintList(ColorStateList.valueOf(
                        Color.parseColor("#FFD700"))); // Vàng
            } else {
                button.setBackgroundTintList(ColorStateList.valueOf(
                        Color.parseColor("#EEEEEE"))); // Xám
            }
        }
    }

}
