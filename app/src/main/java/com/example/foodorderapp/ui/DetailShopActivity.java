package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DetailShopActivity extends AppCompatActivity {
    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> foodList;
    private List<FoodModel> fullFoodList;
    private List<Button> categoryButtons = new ArrayList<>();
    FoodService foodService = new FoodService();
    ShopService shopService = new ShopService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_detail_shop);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.shopdetail), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo danh sách và RecyclerView
        fullFoodList = new ArrayList<>();
        foodList = new ArrayList<>();
        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hotFoodAdapter = new HotFoodAdapter(this, foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        // Khởi tạo danh sách nút danh mục
        categoryButtons.add(findViewById(R.id.btnAll));
        categoryButtons.add(findViewById(R.id.btnSpaghetti));
        categoryButtons.add(findViewById(R.id.btnPotato));
        categoryButtons.add(findViewById(R.id.btnPizza));
        categoryButtons.add(findViewById(R.id.btnBurger));
        categoryButtons.add(findViewById(R.id.btnChicken));
        categoryButtons.add(findViewById(R.id.btnDrink));

        // Thiết lập sự kiện click cho các nút
        setupCategoryButtons();

        // Lấy storeId từ Intent
        int storeId = getIntent().getIntExtra("storeId", -1);

        // Lấy thông tin cửa hàng
        shopService.getShopById(storeId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot shopDoc = queryDocumentSnapshots.getDocuments().get(0);
                        String shopName = shopDoc.getString("shopName");
                        Double rating = shopDoc.getDouble("rating");
                        String ad = shopDoc.getString("advertisement");
                        String imageUrl = shopDoc.getString("imageUrl");

                        TextView tvShopName = findViewById(R.id.tvFoodName);
                        TextView tvShopRating = findViewById(R.id.tvRate);
                        TextView tvShopAd = findViewById(R.id.tvDesc);
                        ImageView imgShop = findViewById(R.id.imageShop);

                        tvShopName.setText(shopName != null ? shopName : "N/A");
                        tvShopRating.setText(rating != null ? String.format("%.1f", rating) : "N/A");
                        tvShopAd.setText(ad != null ? ad : "");

                        if (imageUrl != null && !imageUrl.isEmpty()) {
                            Glide.with(this).load(imageUrl).into(imgShop);
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải thông tin cửa hàng", Toast.LENGTH_SHORT).show();
                });

        // Lấy danh sách món ăn và danh mục
        foodService.getFoodsByStoreId(storeId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    fullFoodList.clear();
                    Set<String> categories = new HashSet<>();

                    for (DocumentSnapshot doc : queryDocumentSnapshots) {
                        FoodModel food = doc.toObject(FoodModel.class);
                        if (food != null) {
                            fullFoodList.add(food);
                            if (food.getCategory() != null) {
                                categories.add(food.getCategory().toLowerCase());
                            }
                        }
                    }

                    // Hiển thị/ẩn các nút danh mục
                    for (Button button : categoryButtons) {
                        String category = button.getText().toString().toLowerCase();
                        if (categories.contains(category) || category.equals("all")) {
                            button.setVisibility(View.VISIBLE);
                        } else {
                            button.setVisibility(View.GONE);
                        }
                    }

                    // Cập nhật danh sách món ăn mặc định
                    foodList.clear();
                    foodList.addAll(fullFoodList);
                    hotFoodAdapter.notifyDataSetChanged();
                    selectCategory("all", findViewById(R.id.btnAll));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải danh sách món ăn", Toast.LENGTH_SHORT).show();
                });
    }

    private void setupCategoryButtons() {
        for (Button button : categoryButtons) {
            button.setOnClickListener(v -> {
                String category = button.getText().toString().toLowerCase();
                selectCategory(category, button);
            });
        }
    }

    private void selectCategory(String category, Button selectedButton) {
        filterFoods(category);
        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // Vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEEEEE)); // Xám nhạt
            }
        }
    }

    private void filterFoods(String category) {
        List<FoodModel> filtered = new ArrayList<>();
        if (category.equals("all")) {
            filtered.addAll(fullFoodList);
        } else {
            for (FoodModel food : fullFoodList) {
                if (food.getCategory() != null && food.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(food);
                }
            }
        }
        foodList.clear();
        foodList.addAll(filtered);
        hotFoodAdapter.notifyDataSetChanged();
    }
}