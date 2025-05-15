package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.ui.LoginActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ImageView[] dots;
    private int currentIndex = 0;
    private Button btnNext, btnCancel;
    private ImageView ivOrder;
    private TextView tv;
    private int[] imageResources;
    private FrameLayout loadingOverlay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_intro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        ShopService shopService = new ShopService();
//        FoodService foodService = new FoodService();
//
//        String[] foodNames = {
//                // Spaghetti
//                "Spaghetti Aglio e Olio", "Spaghetti Carbonara", "Spaghetti Bolognese", "Spaghetti Pesto", "Spaghetti Marinara",
//                // Potato
//                "Potato Fries", "Mashed Potatoes", "Baked Potato", "Potato Salad", "Potato Wedges",
//                // Pizza
//                "Pepperoni Pizza", "Margherita Pizza", "Hawaiian Pizza", "Vegetarian Pizza", "BBQ Chicken Pizza",
//                // Burger
//                "Cheeseburger", "Chicken Burger", "Veggie Burger", "Double Cheeseburger",
//                // Chicken
//                "Chicken Nuggets",
//                // Drink
//                "Coca-Cola", "Pepsi", "Orange Juice", "Lemon Tea", "Mineral Water"
//        };
//
//        String[] categories = {
//                "Spaghetti", "Spaghetti", "Spaghetti", "Spaghetti", "Spaghetti",
//                "Potato", "Potato", "Potato", "Potato", "Potato",
//                "Pizza", "Pizza", "Pizza", "Pizza", "Pizza",
//                "Burger", "Burger", "Burger", "Burger",
//                "Chicken",
//                "Drink", "Drink", "Drink", "Drink", "Drink"
//        };
//
//        String[] imageUrls = {
//                // Spaghetti
//                "https://www.simplyrecipes.com/thmb/Q9Z4ZqQmMNH50t9EBT9Z7pQgZMk=/2000x1500/filters:no_upscale():max_bytes(150000):strip_icc()/Simply-Recipes-Spaghetti-Aglio-e-Olio-LEAD-3-fabea0df519d4a2cb4b16f1c6aa7e948.jpg",
//                "https://www.recipetineats.com/wp-content/uploads/2019/10/Spaghetti-Carbonara_2.jpg",
//                "https://www.spendwithpennies.com/wp-content/uploads/2022/07/Spaghetti-Bolognese-SpendWithPennies-20.jpg",
//                "https://www.acouplecooks.com/wp-content/uploads/2021/09/Spaghetti-Pesto-008.jpg",
//                "https://feelgoodfoodie.net/wp-content/uploads/2020/04/Spaghetti-Marinara-8.jpg",
//                // Potato
//                "https://www.simplyrecipes.com/thmb/VXqPyGXE0ZGVBDEtkckW-1RGSAQ=/2000x1500/filters:no_upscale():max_bytes(150000):strip_icc()/Simply-Recipes-French-Fries-LEAD-4-4c7083162cfb4e0f8b1cb3892d216fa2.jpg",
//                "https://www.thechunkychef.com/wp-content/uploads/2021/11/Creamy-Mashed-Potatoes-8.jpg",
//                "https://www.acouplecooks.com/wp-content/uploads/2021/06/Baked-Potato-001.jpg",
//                "https://www.simplyrecipes.com/thmb/DN_P_7kGvDFFeFbbZf6v2BlfjSg=/2000x1333/filters:no_upscale():max_bytes(150000):strip_icc()/Simply-Recipes-Potato-Salad-LEAD-08-21429d349a8b4a2a9a8dcf1b129f8437.jpg",
//                "https://www.recipetineats.com/wp-content/uploads/2022/02/Potato-Wedges_7-SQ.jpg",
//                // Pizza
//                "https://upload.wikimedia.org/wikipedia/commons/d/d1/Pepperoni_pizza.jpg",
//                "https://www.acouplecooks.com/wp-content/uploads/2022/08/Margherita-Pizza-014.jpg",
//                "https://www.recipetineats.com/wp-content/uploads/2020/05/Hawaiian-Pizza_0.jpg",
//                "https://www.spendwithpennies.com/wp-content/uploads/2022/03/Vegetarian-Pizza-SpendWithPennies-7.jpg",
//                "https://www.simplyrecipes.com/thmb/W1EGYxY20GBDr82a9PdAcIpXgOA=/2000x1500/filters:no_upscale():max_bytes(150000):strip_icc()/Simply-Recipes-BBQ-Chicken-Pizza-LEAD-07-9f5311cd1dfc4d99a39039e2f709b83c.jpg",
//                // Burger
//                "https://upload.wikimedia.org/wikipedia/commons/0/0b/RedDot_Burger.jpg",
//                "https://www.spendwithpennies.com/wp-content/uploads/2021/08/Grilled-Chicken-Burgers-SpendWithPennies-22.jpg",
//                "https://cookieandkate.com/images/2019/04/veggie-burgers-recipe-4-768x1154.jpg",
//                "https://www.seriouseats.com/thmb/3nDRbY4p0E2Jc82YXSGvFbeWgVY=/1500x1125/filters:no_upscale():max_bytes(150000):strip_icc()/20230503-Double-Cheeseburger-Jillian-Atkinson-hero-a5d5a5e6c77b4d858aee5c9e6b9bfb7e.jpg",
//                // Chicken
//                "https://www.seriouseats.com/thmb/Ew0InxB6PBfiFK3HNRPIdG63RJ8=/1500x1125/filters:no_upscale():max_bytes(150000):strip_icc()/20230713-Chicken-Nuggets-Jillian-Atkinson-hero-1c6e9a39ba3e4900989f764d6c60b643.jpg",
//                // Drink
//                "https://upload.wikimedia.org/wikipedia/commons/0/0b/Coca-Cola_Bottle.JPG",
//                "https://upload.wikimedia.org/wikipedia/commons/2/2f/Pepsi_can_2020.jpg",
//                "https://upload.wikimedia.org/wikipedia/commons/6/66/Orange_Juice_1.jpg",
//                "https://upload.wikimedia.org/wikipedia/commons/b/b3/Teh_O_Ais_Limau.jpg",
//                "https://upload.wikimedia.org/wikipedia/commons/c/cd/Bottled_water.JPG"
//        };
//
//        for (int i = 0; i < foodNames.length; i++) {
//            FoodModel food = new FoodModel();
//            food.setFoodId(i + 1);
//            int storeId = (i % 12) + 1;
//            food.setStoreId(storeId);
//            food.setName(foodNames[i]);
//            food.setPrice(100);
//            food.setRating(0f);
//            food.setSold(0);
//            food.setCategory(categories[i]);
//            food.setImageUrl(imageUrls[i]);
//
//            foodService.addFood(food);
//        }

//
//        List<ShopModel> shopList = new ArrayList<>();
//
//        // Các cửa hàng ăn nhanh kiểu Jolibi với link ảnh thực tế
//        shopList.add(new ShopModel(1, "Jolibi - Gà Rán", "68 Nguyễn Thiện Thuật, Quận 3, TP.HCM", 10, "https://example.com/jolibi1.jpg", "Gà rán, khoai tây chiên, và nước ngọt", 0));
//        shopList.add(new ShopModel(2, "Gà Rán Cô Ba", "91 Đường Trần Hưng Đạo, Quận 1, TP.HCM", 15, "https://example.com/garancoba1.jpg", "Gà rán giòn, mềm, ngon", 0));
//        shopList.add(new ShopModel(3, "KFC Việt Nam", "104 Nguyễn Văn Cừ, Quận 5, TP.HCM", 5, "https://example.com/kfc1.jpg", "Gà rán ngon, hấp dẫn", 0));
//        shopList.add(new ShopModel(4, "McDonald's", "Số 33 Nguyễn Trãi, Quận 1, TP.HCM", 10, "https://example.com/mcdonalds1.jpg", "Burger và khoai tây chiên", 0));
//        shopList.add(new ShopModel(5, "Lotteria", "9 Lê Thánh Tôn, Quận 1, TP.HCM", 20, "https://example.com/lotteria1.jpg", "Gà rán và các món ăn nhanh", 0));
//        shopList.add(new ShopModel(6, "Jollibee", "160 Trần Hưng Đạo, Quận 5, TP.HCM", 15, "https://example.com/jollibee1.jpg", "Gà rán, bánh mỳ, khoai tây chiên", 0));
//        shopList.add(new ShopModel(7, "Popeyes", "Số 68 Trần Quang Khải, Quận 1, TP.HCM", 10, "https://example.com/popeyes1.jpg", "Gà rán kiểu Mỹ", 0));
//        shopList.add(new ShopModel(8, "Cơm Gà Hưng Béo", "102 Lê Thánh Tôn, Quận 1, TP.HCM", 20, "https://example.com/comga1.jpg", "Cơm gà ngon, thơm", 0));
//        shopList.add(new ShopModel(9, "Gà Rán Sài Gòn", "56 Lý Tự Trọng, Quận 1, TP.HCM", 5, "https://example.com/garan1.jpg", "Gà rán giòn, thơm", 0));
//        shopList.add(new ShopModel(10, "Gà Rán Xôi", "23 Nguyễn Thái Bình, Quận 1, TP.HCM", 25, "https://example.com/garanxoi1.jpg", "Gà rán và xôi đầy đặn", 0));
//        shopList.add(new ShopModel(11, "KFC Lẩu Gà", "35 Nguyễn Văn Cừ, Quận 5, TP.HCM", 20, "https://example.com/kfclau1.jpg", "Lẩu gà và gà rán", 0));
//        shopList.add(new ShopModel(12, "Gà Rán Hoàng Gia", "98 Nguyễn Hữu Cảnh, Bình Thạnh, TP.HCM", 10, "https://example.com/garanhoanggia1.jpg", "Gà rán giòn, khoai tây chiên", 0));
//
//        // Dùng vòng lặp for để gọi hàm addShop
//        for (ShopModel shop : shopList) {
//            shopService.addShop(shop);
//        }
        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingOverlay.setVisibility(View.GONE);
        ProgressBar progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        imageResources = new int[]{
                R.drawable.order,
                R.drawable.chef,
                R.drawable.delivery
        };

        tv = findViewById(R.id.textView);

        ivOrder = findViewById(R.id.iv_order);
        dots = new ImageView[] {
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3)
        };
        updateDots();

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            currentIndex = currentIndex + 1;
            if (currentIndex < imageResources.length) {
                ivOrder.setImageResource(imageResources[currentIndex]);
                if (currentIndex == 0) {
                    tv.setText("Tất cả sở thích của bạn");
                } else if (currentIndex == 1) {
                    tv.setText("Đặt món đến từ đầu bếp");
                } else if (currentIndex == 2) {
                    tv.setText("Miễn phí vận chuyển");
                }
                updateDots();
            } else {
                loadingOverlay.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingOverlay.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.VISIBLE);

                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == currentIndex);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Ẩn progress bar nếu nó đang hiển thị
        ProgressBar progressBar = findViewById(R.id.progressBar);
        FrameLayout loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingOverlay.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);  // Đảm bảo rằng progress bar không hiển thị khi quay lại
    }
}
