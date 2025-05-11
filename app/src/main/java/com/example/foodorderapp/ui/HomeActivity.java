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
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private static final String TAG = "HomeActivity";
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        userService = new UserService();
        foodService = new FoodService();

        // Setup thanh padding tránh che mất layout bởi status/navigation bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.home), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Hiển thị thông tin người dùng
        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String name = documentSnapshot.getString("fullName");
                String addr = documentSnapshot.getString("address");
                TextView nameTextView = findViewById(R.id.txt_name);
                TextView addressTextView = findViewById(R.id.txt_address);
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
        hotFoodAdapter = new HotFoodAdapter(foodList);
        recyclerHotFood.setAdapter(hotFoodAdapter);
        loadAllFoods();
        // Khởi tạo danh sách shop giảm giá (dữ liệu tạm hardcode)
        fullShopList = new ArrayList<>();
        fullShopList.add(new ShopModel(1, "Burger King", "123 Le Loi, Da Nang", 10.0f, "burger1", "Giảm 10% cho combo siêu ngon hôm nay!", Arrays.asList("burger", "fastfood", "drink")));
        fullShopList.add(new ShopModel(2, "Peppe Pizzeria", "45 Tran Phu, Da Nang", 15.0f, "pizza1", "Pizza nướng lò chuẩn Ý – Mua 2 tặng 1!", Arrays.asList("pizza", "fastfood", "dessert")));
        fullShopList.add(new ShopModel(3, "KFC", "78 Nguyen Van Linh, Da Nang", 12.0f, "chicken1", "Gà rán giòn rụm – Free Pepsi cho hóa đơn trên 100k!", Arrays.asList("fried chicken", "burger", "drink")));
        shopList = new ArrayList<>(fullShopList);
        recyclerSaleShop = findViewById(R.id.recyclerSaleShop);
        recyclerSaleShop.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        saleShopAdapter = new SaleShopAdapter(shopList);
        recyclerSaleShop.setAdapter(saleShopAdapter);

        // Gán sự kiện cho các button lọc
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
                        R.drawable.burger1,
                        doc.getLong("sold").intValue(),
                        doc.getString("category")
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
        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEEEEE)); // xám nhạt
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
}
