package com.example.foodorderapp;
import android.util.Log;
import androidx.annotation.NonNull;
import com.google.android.gms.tasks.OnFailureListener;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.adapter.SaleShopAdapter;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";

    private RecyclerView recyclerHotFood;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private HotFoodAdapter hotFoodAdapter;
    private List<FoodModel> foodList;         // Danh sách hiện tại
    private List<FoodModel> fullFoodList;     // Danh sách gốc để lọc

    private List<ShopModel> fullShopList;     // Danh sách gốc để lọc
    private RecyclerView recyclerSaleShop;
    private SaleShopAdapter saleShopAdapter;
    private List<ShopModel> shopList;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken;
    private List<Button> categoryButtons;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Create a new user with a first and last name
        Map<String, Object> user = new HashMap<>();
        user.put("first", "Ada");
        user.put("last", "Lovelace");
        user.put("born", 1815);

// Add a new document with a generated ID
        db.collection("users")
                .add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });

        recyclerHotFood = findViewById(R.id.recyclerHotFood);
        recyclerHotFood.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        fullFoodList = new ArrayList<>();
        fullFoodList.add(new FoodModel(1, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger1, 120, "Burger"));
        fullFoodList.add(new FoodModel(2, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza1, 85, "Pizza"));
        fullFoodList.add(new FoodModel(3, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(4, 102, "Mỳ Ý", "110.000đ", 4.1f, R.drawable.spaggetti1, 55, "Spaghetti"));
        fullFoodList.add(new FoodModel(5, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(6, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger1, 120, "Burger"));
        fullFoodList.add(new FoodModel(7, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza1, 85, "Pizza"));
        fullFoodList.add(new FoodModel(8, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(9, 102, "Mỳ Ý", "110.000đ", 4.1f, R.drawable.spaggetti1, 55, "Spaghetti"));
        fullFoodList.add(new FoodModel(10, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(11, 101, "Burger khổng lồ", "100.000đ", 4.2f, R.drawable.burger1, 120, "Burger"));
        fullFoodList.add(new FoodModel(12, 101, "Pizza phô mai", "150.000đ", 4.5f, R.drawable.pizza1, 85, "Pizza"));
        fullFoodList.add(new FoodModel(13, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        fullFoodList.add(new FoodModel(14, 102, "Mỳ Ý", "110.000đ", 4.1f, R.drawable.spaggetti1, 55, "Spaghetti"));
        fullFoodList.add(new FoodModel(15, 102, "Gà rán giòn", "90.000đ", 4.0f, R.drawable.chicken1, 200, "Chicken"));
        foodList = new ArrayList<>(fullFoodList);

        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);

        // Setup Recycler Sale Shop
        fullShopList = new ArrayList<>();

        fullShopList.add(new ShopModel(
                1,
                "Burger King",
                "123 Le Loi, Da Nang",
                10.0f,
                "burger1", // tên ảnh trong drawable
                "Giảm 10% cho combo siêu ngon hôm nay!",
                Arrays.asList("burger", "fastfood", "drink")
        ));

        fullShopList.add(new ShopModel(
                2,
                "Peppe Pizzeria",
                "45 Tran Phu, Da Nang",
                15.0f,
                "pizza1",
                "Pizza nướng lò chuẩn Ý – Mua 2 tặng 1!",
                Arrays.asList("pizza", "fastfood", "dessert")
        ));

        fullShopList.add(new ShopModel(
                3,
                "KFC",
                "78 Nguyen Van Linh, Da Nang",
                12.0f,
                "chicken1",
                "Gà rán giòn rụm – Free Pepsi cho hóa đơn trên 100k!",
                Arrays.asList("fried chicken", "burger", "drink")
        ));

// Danh sách cửa hàng hiện tại sẽ là một bản sao của fullShopList
        List<ShopModel> shopList = new ArrayList<>(fullShopList);

// Set adapter cho RecyclerView
        recyclerSaleShop = findViewById(R.id.recyclerSaleShop);
        recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

// Tạo và gắn adapter cho RecyclerView
        SaleShopAdapter saleShopAdapter = new SaleShopAdapter(shopList);
        recyclerSaleShop.setAdapter(saleShopAdapter);

        // Setup Buttons
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);

        categoryButtons = new ArrayList<>();
        categoryButtons.add(btnAll);
        categoryButtons.add(btnSpaghetti);
        categoryButtons.add(btnPotato);
        categoryButtons.add(btnPizza);
        categoryButtons.add(btnBurger);
        categoryButtons.add(btnChicken);

        // Button click listeners
        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));
    }

    private void selectCategory(String category, Button selectedButton) {
        // Lọc thực phẩm
        filterFoods(category);
        // Lọc shop
//        filterShops(category);

        // Cập nhật màu sắc của các button category
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

//    private void filterShops(String category) {
//        List<ShopModel> filteredShopList = new ArrayList<>();
//
//        if (category.equals("Tất cả")) {
//            filteredShopList.addAll(fullShopList);  // fullShopList là danh sách tất cả các shop
//        } else {
//            for (ShopModel shop : fullShopList) {
//                // Kiểm tra xem danh sách các category của shop có chứa category được chọn không
//                if (shop.getCategories().contains(category)) {
//                    filteredShopList.add(shop);
//                }
//            }
//        }
//        saleShopAdapter.updateData(filteredShopList);  // Cập nhật lại adapter
//
//    }
}

