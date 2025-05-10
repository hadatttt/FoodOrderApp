package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.adapter.SaleShopAdapter;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerHotFood;
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> foodList;
    private List<FoodModel> fullFoodList;

    private List<ShopModel> fullShopList;
    private RecyclerView recyclerSaleShop;
    private SaleShopAdapter saleShopAdapter;
    private List<ShopModel> shopList;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken;
    private List<Button> categoryButtons;

    private TextView tvAllHot;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    UserService userService = new UserService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        UserService userService = new UserService();
        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName"); // Hoặc documentSnapshot.get("name").toString();
                String addr = documentSnapshot.getString("address");
                TextView nameTextView = findViewById(R.id.txt_name);
                TextView addressTextView = findViewById(R.id.txt_address);
                nameTextView.setText("Chào " +name+", Ngon Miệng Nhé");
                addressTextView.setText(addr);
            } else {
                // Document không tồn tại
                Log.d("UserService", "User document does not exist.");
            }
        }).addOnFailureListener(e -> {
            // Xử lý lỗi
            Log.e("UserService", "Failed to get user data", e);
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        // Hot food setup
        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fullFoodList = new ArrayList<>();
        fullFoodList.add(new FoodModel(1, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger1, 120, "Burger"));
        fullFoodList.add(new FoodModel(2, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza1, 85, "Pizza"));
        fullFoodList.add(new FoodModel(3, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(4, 102, "Mỳ Ý", "110.000đ", 4.1f, R.drawable.spaggetti1, 55, "Spaghetti"));
        fullFoodList.add(new FoodModel(5, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        foodList = new ArrayList<>(fullFoodList);

        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        // Sale shop setup
        fullShopList = new ArrayList<>();
        fullShopList.add(new ShopModel(1, "Burger King", "123 Le Loi, Da Nang", 10.0f, "burger1", "Giảm 10% cho combo siêu ngon hôm nay!", Arrays.asList("burger", "fastfood", "drink")));
        fullShopList.add(new ShopModel(2, "Peppe Pizzeria", "45 Tran Phu, Da Nang", 15.0f, "pizza1", "Pizza nướng lò chuẩn Ý – Mua 2 tặng 1!", Arrays.asList("pizza", "fastfood", "dessert")));
        fullShopList.add(new ShopModel(3, "KFC", "78 Nguyen Van Linh, Da Nang", 12.0f, "chicken1", "Gà rán giòn rụm – Free Pepsi cho hóa đơn trên 100k!", Arrays.asList("fried chicken", "burger", "drink")));

        shopList = new ArrayList<>(fullShopList);

        recyclerSaleShop = findViewById(R.id.recyclerSaleShop);
        recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        saleShopAdapter = new SaleShopAdapter(shopList);
        recyclerSaleShop.setAdapter(saleShopAdapter);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);

        categoryButtons = Arrays.asList(btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken);

        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));

        // "Thêm >" textView click
        tvAllHot = findViewById(R.id.allHot);
        tvAllHot.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllHotFoodActivity.class);
            startActivity(intent);
        });
        tvAllHot = findViewById(R.id.allSale);
        tvAllHot.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AllShopSaleActivity.class);
            startActivity(intent);
        });
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
