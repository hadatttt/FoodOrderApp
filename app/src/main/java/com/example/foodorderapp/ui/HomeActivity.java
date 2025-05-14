package com.example.foodorderapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.adapter.SaleShopAdapter;
import com.example.foodorderapp.databinding.ActivityHomeBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private ActivityHomeBinding binding;
    private HotFoodAdapter hotFoodAdapter;
    private SaleShopAdapter saleShopAdapter;
    private List<FoodModel> foodList = new ArrayList<>();
    private List<FoodModel> fullFoodList = new ArrayList<>();
    private List<ShopModel> shopList = new ArrayList<>();
    private List<ShopModel> fullShopList = new ArrayList<>();
    private List<Button> categoryButtons;
    private UserService userService;
    private FoodService foodService;
    private ShopService shopService;
    private Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initializeServices();
        setupWindowInsets();
        setupUserInfo();
        setupRecyclerViews();
        setupCategoryButtons();
        setupNavigation();
        loadCartItems();
    }

    private void initializeServices() {
        userService = new UserService();
        foodService = new FoodService();
        shopService = new ShopService();
    }

    private void setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupUserInfo() {
        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String addr = documentSnapshot.getString("address");
                binding.tvFullName.setText("Chào " + name + ", Ngon Miệng Nhé");
                binding.tvAddress.setText(addr);
            } else {
                Log.d(TAG, "User document does not exist.");
            }
        }).addOnFailureListener(e -> Log.e(TAG, "Failed to get user data", e));
    }

    private void setupRecyclerViews() {
        // Hot Food RecyclerView
        binding.recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        hotFoodAdapter = new HotFoodAdapter(context, foodList);
        binding.recyclerHotFood.setAdapter(hotFoodAdapter);
        loadAllFoods();

        // Sale Shop RecyclerView
        binding.recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        saleShopAdapter = new SaleShopAdapter(this, shopList);
        binding.recyclerSaleShop.setAdapter(saleShopAdapter);
        loadAllShops();
    }

    private void setupCategoryButtons() {
        categoryButtons = Arrays.asList(
                binding.btnAll, binding.btnSpaghetti, binding.btnPotato,
                binding.btnPizza, binding.btnBurger, binding.btnChicken, binding.btnDrink
        );

        binding.btnAll.setOnClickListener(v -> selectCategory("Tất cả", binding.btnAll));
        binding.btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", binding.btnSpaghetti));
        binding.btnPotato.setOnClickListener(v -> selectCategory("Potato", binding.btnPotato));
        binding.btnPizza.setOnClickListener(v -> selectCategory("Pizza", binding.btnPizza));
        binding.btnBurger.setOnClickListener(v -> selectCategory("Burger", binding.btnBurger));
        binding.btnChicken.setOnClickListener(v -> selectCategory("Chicken", binding.btnChicken));
        binding.btnDrink.setOnClickListener(v -> selectCategory("Drink", binding.btnDrink));
    }

    private void setupNavigation() {
        binding.allHot.setOnClickListener(v -> startActivity(new Intent(this, AllHotFoodActivity.class)));
        binding.allSale.setOnClickListener(v -> startActivity(new Intent(this, AllShopSaleActivity.class)));
        binding.imgCartIcon.setOnClickListener(v -> startActivity(new Intent(this, CartActivity.class)));
        binding.mainSearchLayout.setOnClickListener(v -> startActivity(new Intent(this, SearchActivity.class)));
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
                        doc.getLong("sold").intValue(),
                        doc.getString("category"),
                        doc.getString("imageUrl"),
                        doc.getString("caption"),
                        (Map<String, Double>) doc.get("sizePrices")
                );
                fullFoodList.add(food);
            }
            foodList.clear();
            foodList.addAll(fullFoodList);
            hotFoodAdapter.notifyDataSetChanged();
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
            filterShops("Tất cả");
        }).addOnFailureListener(e -> Log.e(TAG, "Error loading shop data", e));
    }

    private void selectCategory(String category, Button selectedButton) {
        filterFoods(category);
        filterShops(category);
        updateButtonAppearance(selectedButton);
    }

    private void filterFoods(String category) {
        List<FoodModel> filtered = new ArrayList<>();
        if (category.equals("Tất cả")) {
            filtered.addAll(fullFoodList);
        } else {
            for (FoodModel food : fullFoodList) {
                if (food.getCategory().equalsIgnoreCase(category)) {
                    filtered.add(food);
                }
            }
        }
        foodList.clear();
        foodList.addAll(filtered);
        hotFoodAdapter.notifyDataSetChanged();
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
                    if (food.getStoreId() == shopId && food.getCategory().equalsIgnoreCase(category)) {
                        hasFood = true;
                        break;
                    }
                }
                if (hasFood) {
                    shopList.add(shop);
                }
            }
        }
        saleShopAdapter.updateData(shopList);
    }

    private void updateButtonAppearance(Button selectedButton) {
        for (Button button : categoryButtons) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    button == selectedButton ? 0xFFFFD700 : 0xFFEEEEEE
            ));
        }
    }

    private void loadCartItems() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        new CartService().getCartByUserId(userId)
                .addOnSuccessListener(querySnapshot -> {
                    int cartItemCount = querySnapshot.size();
                    binding.textCartCount.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
                    if (cartItemCount > 0) {
                        binding.textCartCount.setText(String.valueOf(cartItemCount));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Error loading cart items", e));
    }
}