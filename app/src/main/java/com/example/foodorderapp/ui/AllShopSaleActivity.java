package com.example.foodorderapp.ui;

import android.os.Bundle;
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
import com.example.foodorderapp.model.ShopModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AllShopSaleActivity extends AppCompatActivity {

    private RecyclerView recyclerShopSale;
    private SaleShopAdapter shopSaleAdapter;
    private List<ShopModel> fullShopList;
    private List<ShopModel> shopList;

    private Button btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken;
    private List<Button> categoryButtons;

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

        // Nút quay về
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            finish(); // Quay về mà không khởi động lại HomeActivity
        });

        // Khởi tạo RecyclerView ShopSale
        recyclerShopSale = findViewById(R.id.recyclerShopSale);
        recyclerShopSale.setLayoutManager(new GridLayoutManager(this, 2));
        // Danh sách cửa hàng giảm giá
        fullShopList = new ArrayList<>();
        shopList = new ArrayList<>(fullShopList);

        shopSaleAdapter = new SaleShopAdapter(shopList);
        recyclerShopSale.setAdapter(shopSaleAdapter);

        // Category buttons
        btnAll = findViewById(R.id.btnAll);
        btnSpaghetti = findViewById(R.id.btnSpaghetti);
        btnPotato = findViewById(R.id.btnPotato);
        btnPizza = findViewById(R.id.btnPizza);
        btnBurger = findViewById(R.id.btnBurger);
        btnChicken = findViewById(R.id.btnChicken);

        categoryButtons = Arrays.asList(btnAll, btnSpaghetti, btnPotato, btnPizza, btnBurger, btnChicken);

        // Set click listeners
        btnAll.setOnClickListener(v -> selectCategory("Tất cả", btnAll));
        btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", btnSpaghetti));
        btnPotato.setOnClickListener(v -> selectCategory("Potato", btnPotato));
        btnPizza.setOnClickListener(v -> selectCategory("Pizza", btnPizza));
        btnBurger.setOnClickListener(v -> selectCategory("Burger", btnBurger));
        btnChicken.setOnClickListener(v -> selectCategory("Chicken", btnChicken));

        // Mặc định chọn All
        selectCategory("Tất cả", btnAll);
    }

    private void selectCategory(String category, Button selectedButton) {
//        filterShops(category);

        for (Button button : categoryButtons) {
            if (button == selectedButton) {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFFFD700)); // vàng
            } else {
                button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFEEEEEE)); // xám nhạt
            }
        }
    }

//    private void filterShops(String category) {
//        List<ShopModel> filteredShopList = new ArrayList<>();
//
//        if (category.equals("Tất cả")) {
//            filteredShopList.addAll(fullShopList);
//        } else {
//            for (ShopModel shop : fullShopList) {
//                if (shop.getCategory().equalsIgnoreCase(category)) {
//                    filteredShopList.add(shop);
//                }
//            }
//        }
//
//        shopSaleAdapter.updateData(filteredShopList);
//    }
}
