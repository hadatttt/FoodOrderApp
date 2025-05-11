package com.example.foodorderapp.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.SaleShopAdapter;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllShopSaleActivity extends AppCompatActivity {

    private RecyclerView recyclerShopSale;
    private SaleShopAdapter shopSaleAdapter;
    private List<ShopModel> fullShopList = new ArrayList<>();
    private List<FoodModel> fullFoodList = new ArrayList<>();
    private List<ShopModel> shopList = new ArrayList<>();
    private ShopService shopService;
    private FoodService foodService;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken, btnDrink;
    private List<Button> categoryButtons;
    private String selectedCategory = "Tất cả";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_all_shop_sale);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.allshopsale), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Khởi tạo Service
        shopService = new ShopService();
        foodService = new FoodService();

        // Setup UI
        setupRecyclerView();
        setupCategoryButtons();
        setupBackButton();

        // Tải dữ liệu ban đầu
        loadAllFoods();
    }

    private void setupRecyclerView() {
        recyclerShopSale = findViewById(R.id.recyclerShopSale);
        recyclerShopSale.setLayoutManager(new GridLayoutManager(this, 2));
        shopSaleAdapter = new SaleShopAdapter(shopList);
        recyclerShopSale.setAdapter(shopSaleAdapter);
    }

    private void setupCategoryButtons() {
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);
        btnDrink = findViewById(R.id.btnDrink);

        categoryButtons = Arrays.asList(btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken, btnDrink);

        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));
        btnDrink.setOnClickListener(v -> selectCategory("Drink", btnDrink));
    }

    private void setupBackButton() {
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }

    private void selectCategory(String category, Button selectedButton) {
        this.selectedCategory = category;
        updateCategoryUI(selectedButton);
        filterShops(category);
    }

    private void updateCategoryUI(Button selectedButton) {
        for (Button button : categoryButtons) {
            int color = (button == selectedButton) ? 0xFFFFD700 : 0xFFEEEEEE; // Vàng / Xám
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));
        }
    }

    private void loadAllFoods() {
        foodService.getAllFoods().addOnSuccessListener(querySnapshot -> {
            fullFoodList.clear();
            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                FoodModel food = document.toObject(FoodModel.class);
                fullFoodList.add(food);
            }
            loadAllShops();
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading food data", e));
    }

    private void loadAllShops() {
        shopService.getAllShops().addOnSuccessListener(querySnapshot -> {
            fullShopList.clear();
            for (DocumentSnapshot doc : querySnapshot) {
                ShopModel shop = new ShopModel(
                        doc.getLong("storeid").intValue(),
                        doc.getString("shopName"),
                        doc.getString("address"),
                        doc.getDouble("discount").floatValue(),
                        doc.getString("imageUrl"),
                        doc.getString("advertisement"),
                        doc.getDouble("rating").floatValue()
                );
                fullShopList.add(shop);
            }
            filterShops(selectedCategory);
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading shop data", e));
    }

    private void filterShops(String category) {
        shopList.clear();

        if (category.equals("Tất cả")) {
            shopList.addAll(fullShopList);
        } else {
            for (ShopModel shop : fullShopList) {
                int shopId = shop.getStoreid();
                boolean hasFood = false;

                for (FoodModel food : fullFoodList) {
                    if (food.getStoreId() == shopId &&
                            food.getCategory().equalsIgnoreCase(category)) {
                        hasFood = true;
                        break;
                    }
                }

                if (hasFood) {
                    shopList.add(shop);
                }
            }
        }

        shopSaleAdapter.updateData(shopList);
    }
}
