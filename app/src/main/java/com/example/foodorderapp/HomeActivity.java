package com.example.foodorderapp;

import android.os.Bundle;
import android.widget.Button;

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
    private RecyclerView recyclerSaleShop;
    private SaleShopAdapter saleShopAdapter; // Updated to SaleShopAdapter
    private List<ShopModel> shopList;
    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken;
    private List<Button> categoryButtons; // Danh sách các Button để quản lý màu
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
//shop
        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);
        recyclerSaleShop= findViewById(R.id.recyclerSaleShop);
        recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        shopList = new ArrayList<>();
        shopList.add(new ShopModel(1, "Burger King", "123 Le Loi, Da Nang", 10.0f, R.drawable.burger));
        shopList.add(new ShopModel(2, "Peppe Pizzeria", "45 Tran Phu, Da Nang", 15.0f, R.drawable.pizza));
        shopList.add(new ShopModel(3, "KFC", "78 Nguyen Van Linh, Da Nang", 12.0f, R.drawable.chicken));

        saleShopAdapter = new SaleShopAdapter(shopList); // Updated to SaleShopAdapter
        recyclerSaleShop.setAdapter(saleShopAdapter);
        // button
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);

        // Thêm các Button vào danh sách để quản lý
        categoryButtons = new ArrayList<>();
        categoryButtons.add(btnAll);
        categoryButtons.add(btnSpaghetti);
        categoryButtons.add(btnPotato);
        categoryButtons.add(btnPizza);
        categoryButtons.add(btnBurger);
        categoryButtons.add(btnChicken);
        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Mỳ ý", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Khoai tây chiên", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Gà rán", btnChicken));

    }private void selectCategory(String category, Button selectedButton) {
        // Lọc dữ liệu


        // Cập nhật màu của các Button
        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // Màu vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEEEEE)); // Màu xám
            }
        }
    }
//    private void filterShops(String category) {
//        shopList.clear();
//        if (category.equals("Tất cả")) {
//            shopList.addAll(fullShopList);
//        } else {
//            for (ShopModel shop : fullShopList) {
//                if (shop.getCategory().equals(category)) {
//                    shopList.add(shop);
//                }
//            }
//        }
//        saleShopAdapter.notifyDataSetChanged();
//    }

}