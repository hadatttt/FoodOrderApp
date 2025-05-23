package com.example.foodorderapp.ui;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
    private RecyclerView recyclerHotFood, recyclerSaleShop;
    private HotFoodAdapter hotFoodAdapter;
    private SaleShopAdapter saleShopAdapter;
    private List<FoodModel> foodList;
    private List<FoodModel> fullFoodList;
    private List<ShopModel> shopList;
    private List<ShopModel> fullShopList;
    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken, btnDrink;
    private List<Button> categoryButtons;
    private TextView tvAllHot, tvAllSale;
    private UserService userService;

    private FoodService foodService;
    private ShopService shopService;
    private Context context = this;
    private ImageView imgCart;
    private TextView tvCartCount;
    private ImageView btnProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService = new UserService();
        foodService = new FoodService();
        shopService = new ShopService(); // Thêm dòng này
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.mainSearchLayout.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, SearchActivity.class);
            startActivity(intent);
        });
        // Hiển thị thông tin người dùng
        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String addr = documentSnapshot.getString("address");
                TextView nameTextView = findViewById(R.id.tv_full_name);
                TextView addressTextView = findViewById(R.id.tv_address);
                nameTextView.setText("Chào " + name + ", Ngon Miệng Nhé");
                addressTextView.setText(addr);
            } else {
                Log.d(TAG, "User document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get user data", e);
        });
        // Khởi tạo danh sách món ăn
        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        fullFoodList = new ArrayList<>();
        foodList = new ArrayList<>();
        hotFoodAdapter = new HotFoodAdapter(context,foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);
        //lay all mon an
        loadAllFoods();
        fullShopList = new ArrayList<>();
        // lay all food
        loadAllShops();
        shopList = new ArrayList<>(fullShopList);
        recyclerSaleShop = findViewById(R.id.recyclerSaleShop);
        recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        saleShopAdapter = new SaleShopAdapter(this,shopList);
        recyclerSaleShop.setAdapter(saleShopAdapter);
        // Gán sự kiện cho các button lọc
        btnAll = findViewById(R.id.btnAll);
        btnProfile = findViewById(R.id.btnProfile);
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
        // Chuyển sang xem toàn bộ hot food
        tvAllHot = findViewById(R.id.allHot);
        tvAllHot.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllHotFoodActivity.class);
            startActivity(intent);
        });
        // Chuyển sang xem toàn bộ shop giảm giá
        tvAllSale = findViewById(R.id.allSale);
        tvAllSale.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllShopSaleActivity.class);
            startActivity(intent);
        });
        imgCart = findViewById(R.id.imgCartIcon);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HomeActivity.this, MenuProfileActivity.class);
                startActivity(intent);
            }
        });
        tvCartCount = findViewById(R.id.textCartCount);
    }
    @Override
    protected void onResume() {
        super.onResume();
        loadCartItems();
        // Hiển thị thông tin người dùng
        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String addr = documentSnapshot.getString("address");
                TextView nameTextView = findViewById(R.id.tv_full_name);
                TextView addressTextView = findViewById(R.id.tv_address);
                nameTextView.setText("Chào " + name + ", Ngon Miệng Nhé");
                addressTextView.setText(addr);
            } else {
                Log.d(TAG, "User document does not exist.");
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get user data", e);
        });
        loadAllFoods();
        loadAllShops();
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
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Lỗi khi tải dữ liệu món ăn", e);
        });

    }

    private void selectCategory(String category, Button selectedButton) {
        filterFoods(category);
        filterShops(category);
        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFE4B5));
            }
        }
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

        saleShopAdapter.updateData(shopList);
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
    private void loadCartItems() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        CartService cartService = new CartService();
        cartService.getCartByUserId(userId)
                .addOnSuccessListener(querySnapshot -> {
                    int cartItemCount = querySnapshot.size(); // Mỗi tài liệu là một món

                    if (cartItemCount > 0) {
                        tvCartCount.setText(String.valueOf(cartItemCount));
                        tvCartCount.setVisibility(View.VISIBLE);
                    } else {
                        tvCartCount.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
//                    Toast.makeText(HomeActivity.this, "Không thể tải giỏ hàng", Toast.LENGTH_SHORT).show();
                });
    }
}