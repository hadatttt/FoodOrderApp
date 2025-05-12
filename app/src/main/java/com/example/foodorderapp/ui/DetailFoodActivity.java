package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
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
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;

public class DetailFoodActivity extends AppCompatActivity {
    // Khai báo các biến thành phần
    private int foodId;
    private int quantity = 1;

    private FoodService foodService;
    private ShopService shopService;
    private UserService userService;
    private CartService cartService;

    private Map<String, Double> sizePrices = new HashMap<>();

    // Khai báo các View
    private TextView tvQuantity, tvPrice, tvFoodName, tvRate, tvDesc, tvStore;
    private ImageView imgFood;
    private RadioButton rbS, rbM, rbL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);

        // Khởi tạo các dịch vụ và các thành phần giao diện
        initializeServices();
        setupViews();
        setupListeners();

        foodId = getIntent().getIntExtra("foodid", -1);

        if (foodId != -1) {
            loadFoodDetails(foodId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn!", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void initializeServices() {
        foodService = new FoodService();
        shopService = new ShopService();
        userService = new UserService();
        cartService = new CartService();
    }

    private void setupViews() {
        // Thiết lập giao diện
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
        // Thiết lập các sự kiện cho nút cộng, trừ số lượng
        findViewById(R.id.btnPlus).setOnClickListener(v -> updateQuantity(1));
        findViewById(R.id.btnMinus).setOnClickListener(v -> updateQuantity(-1));

        // Lắng nghe sự kiện thay đổi lựa chọn kích thước
        RadioGroup rgSize = findViewById(R.id.rgSize);
        rgSize.setOnCheckedChangeListener((group, checkedId) -> updatePriceBasedOnSize());

        // Sự kiện "Thêm vào giỏ hàng"
        findViewById(R.id.btnAddToCart).setOnClickListener(v -> addToCart());
    }

    private void updateQuantity(int delta) {
        quantity = Math.max(1, quantity + delta);
        tvQuantity.setText(String.valueOf(quantity));
        updatePriceBasedOnSize();
    }

    private void addToCart() {
        String size = getSelectedSize();
        double price = sizePrices.getOrDefault(size, 0.0);
        double totalPrice = price * quantity;

        // Lấy thông tin người dùng và thêm vào giỏ hàng
        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userId = task.getResult().getId();
                CartModel cartModel = new CartModel(foodId, size, quantity, totalPrice, userId);
                addCartItem(cartModel);
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addCartItem(CartModel cartModel) {
        cartService.addToCart(cartModel).addOnCompleteListener(cartTask -> {
            if (cartTask.isSuccessful()) {
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Lỗi khi thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updatePriceBasedOnSize() {
        double price = sizePrices.getOrDefault(getSelectedSize(), 0.0);
        double total = price * quantity;
        tvPrice.setText("Giá: " + String.format("%.3f", total) + "đ");
    }

    private String getSelectedSize() {
        if (rbS.isChecked()) return "S";
        if (rbM.isChecked()) return "M";
        if (rbL.isChecked()) return "L";
        return "";
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

        // Hiển thị thông tin món ăn
        tvFoodName.setText(name != null ? name : "Tên món");
        tvDesc.setText(caption != null ? caption : "Mô tả");
        tvRate.setText(String.valueOf(rating != null ? rating : 0.0f));

        // Cập nhật giá dựa trên kích thước
        Map<String, Long> rawSizePrices = (Map<String, Long>) doc.get("sizePrices");
        if (rawSizePrices != null) {
            for (Map.Entry<String, Long> entry : rawSizePrices.entrySet()) {
                sizePrices.put(entry.getKey(), entry.getValue().doubleValue());
            }
            updatePriceBasedOnSize();
        }
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
            } else {
                Toast.makeText(this, "Không tìm thấy cửa hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
