package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class DetailFoodActivity extends AppCompatActivity {

    private int quantity = 1;
    private final FoodService foodService = new FoodService();
    private final ShopService shopService = new ShopService();
    private final UserService userService = new UserService();
    private Map<String, Double> sizePrices = new HashMap<>();

    private TextView tvQuantity, tvPrice, tvFoodName, tvRate, tvDesc, tvStore;
    private ImageView imgFood;
    private RadioButton rbS, rbM, rbL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        setupInsets();
        setupViews();
        setupListeners();

        int foodId = getIntent().getIntExtra("foodid", -1);
        if (foodId != -1) {
            loadFoodDetails(foodId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void setupViews() {
        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));

        imgFood = findViewById(R.id.imgFood);
        tvFoodName = findViewById(R.id.tvFoodName);
        tvRate = findViewById(R.id.tvRate);
        tvDesc = findViewById(R.id.tvDesc);
        tvStore = findViewById(R.id.tvStore);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvPrice = findViewById(R.id.tvPrice);
        rbS = findViewById(R.id.sizeSmall);
        rbM = findViewById(R.id.sizeMedium);
        rbL = findViewById(R.id.sizeLarge);

        tvQuantity.setText(String.valueOf(quantity));
    }

    private void setupListeners() {
        findViewById(R.id.btnPlus).setOnClickListener(v -> {
            quantity++;
            tvQuantity.setText(String.valueOf(quantity));
        });

        findViewById(R.id.btnMinus).setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                tvQuantity.setText(String.valueOf(quantity));
            }
        });

        RadioGroup rgSize = findViewById(R.id.rgSize);
        rgSize.setOnCheckedChangeListener((group, checkedId) -> updatePriceBasedOnSize());
    }

    private void updatePriceBasedOnSize() {
        if (rbS.isChecked() && sizePrices.containsKey("S")) {
            tvPrice.setText("Giá: " + sizePrices.get("S").intValue() + "đ");
        } else if (rbM.isChecked() && sizePrices.containsKey("M")) {
            tvPrice.setText("Giá: " + sizePrices.get("M").intValue() + "đ");
        } else if (rbL.isChecked() && sizePrices.containsKey("L")) {
            tvPrice.setText("Giá: " + sizePrices.get("L").intValue() + "đ");
        }
    }

    private void loadFoodDetails(int foodId) {
        foodService.getFoodDetails(foodId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                FoodModel food = doc.toObject(FoodModel.class);

                if (food != null) {
                    displayFoodDetails(doc, food);
                    loadShopInfo(doc.getLong("storeId"));
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void displayFoodDetails(DocumentSnapshot doc, FoodModel food) {
        String imageUrl = doc.getString("imageUrl");
        String name = doc.getString("name");
        String caption = doc.getString("caption");
        Double rating = doc.getDouble("rating");

        // Load ảnh
        Glide.with(this).load(imageUrl).into(imgFood);

        // Hiển thị thông tin
        tvFoodName.setText(name != null ? name : "Tên món");
        tvDesc.setText(caption != null ? caption : "Mô tả");
        tvRate.setText(String.valueOf(rating != null ? rating : 0.0f));

        // Load giá size
        Map<String, Long> rawSizePrices = (Map<String, Long>) doc.get("sizePrices");
        if (rawSizePrices != null) {
            for (Map.Entry<String, Long> entry : rawSizePrices.entrySet()) {
                sizePrices.put(entry.getKey(), entry.getValue().doubleValue());
            }
            // Mặc định chọn size S nếu có
            if (sizePrices.containsKey("S")) rbS.setChecked(true);
            else if (sizePrices.containsKey("M")) rbM.setChecked(true);
            else if (sizePrices.containsKey("L")) rbL.setChecked(true);

            updatePriceBasedOnSize();
        }

        Log.d("DetailFood", "Tên món: " + name + ", Đánh giá: " + rating);
    }

    private void loadShopInfo(Long storeIdLong) {
        if (storeIdLong == null) return;

        int storeId = storeIdLong.intValue();
        shopService.getShopById(storeId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot shopDoc = task.getResult().getDocuments().get(0);
                String shopName = shopDoc.getString("shopName");
                String address = shopDoc.getString("address");

                tvStore.setText(shopName != null ? shopName : "Tên cửa hàng");
                Log.d("DetailShop", "Shop: " + shopName + ", Địa chỉ: " + address);
            } else {
                Toast.makeText(this, "Không tìm thấy cửa hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
