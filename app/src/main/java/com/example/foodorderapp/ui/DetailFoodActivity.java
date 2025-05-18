package com.example.foodorderapp.ui;

import static android.app.PendingIntent.getActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
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
import com.example.foodorderapp.service.MapService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class DetailFoodActivity extends AppCompatActivity {
    // Khai báo các biến thành phần
    private int foodId;
    private int quantity = 1;
    private ListenerRegistration cartListener;
    private FoodService foodService;
    private ShopService shopService;
    private UserService userService;
    private CartService cartService;

    private Map<String, Double> sizePrices = new HashMap<>();

    // Khai báo các View
    private TextView tvQuantity, tvPrice, tvFoodName, tvRate, tvDesc, tvStore;
    private ImageView imgFood;
    private RadioButton rbS, rbM, rbL;
    private LinearLayout llSizeRow;
    private ProgressBar progressTime;
    private TextView tvTime;
    private ImageView imgCart;
    private TextView tvCartCount;
    private final MapService mapService = new MapService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        // Khởi tạo các dịch vụ và các thành phần giao diện
        initializeServices();
        setupViews();
        setupListeners();
        imgCart = findViewById(R.id.imgCartIcon);
        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailFoodActivity.this, CartActivity.class);
                startActivity(intent);
            }
        });
        foodId = getIntent().getIntExtra("foodId", -1);

        if (foodId != -1) {
            loadFoodDetails(foodId);
        } else {
            Toast.makeText(this, "Không tìm thấy món ăn! id:" + foodId, Toast.LENGTH_SHORT).show();
            finish();
        }
        loadCartItemsRealtime();
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
//        btnBack.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
        llSizeRow = findViewById(R.id.llSizeRow);
        imgCart = findViewById(R.id.imgCartIcon);
        tvTime = findViewById(R.id.time);
        tvCartCount = findViewById(R.id.textCartCount);
        progressTime = findViewById(R.id.progressTime);

        tvQuantity.setText(String.valueOf(quantity));
    }
    private void setupTime(String shopAddress) {
        runOnUiThread(() -> {
            progressTime.setVisibility(View.VISIBLE);
            tvTime.setVisibility(View.GONE);
        });

        UserService userService = new UserService();

        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String userAddress = documentSnapshot.getString("address");

                if (userAddress != null && !userAddress.isEmpty()) {
                    mapService.getCoordinatesFromAddress(userAddress, (userLat, userLng) -> {
                        mapService.getCoordinatesFromAddress(shopAddress, (shopLat, shopLng) -> {
                            if (userLat != 0 && userLng != 0 && shopLat != 0 && shopLng != 0) {
                                mapService.getTravelTimeOSRM(userLat, userLng, shopLat, shopLng, this::showTimeResult);
                            } else {
                                showTimeResult("--");
                            }
                        });
                    });
                } else {
                    showTimeResult("--");
                }
            } else {
                showTimeResult("--");
            }
        }).addOnFailureListener(e -> {
            showTimeResult("--");
        });
    }

    private void showTimeResult(String text) {
        runOnUiThread(() -> {
            progressTime.setVisibility(View.GONE);
            tvTime.setVisibility(View.VISIBLE);
            tvTime.setText(text);
        });
    }
    private void setupListeners() {
        // Thiết lập các sự kiện cho nút cộng, trừ số lượng
        findViewById(R.id.btnPlus).setOnClickListener(v -> updateQuantity(1));
        findViewById(R.id.btnMinus).setOnClickListener(v -> updateQuantity(-1));

        // Lắng nghe sự kiện thay đổi lựa chọn kích thước
        RadioGroup rgSize = findViewById(R.id.rgSize);
        rgSize.setOnCheckedChangeListener((group, checkedId) -> updatePriceBasedOnSize());

        // Sự kiện "Thêm vào giỏ hàng"
        findViewById(R.id.btnAddToCart).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addToCart();
            }
        });

    }

    private void updateQuantity(int delta) {
        quantity = Math.max(1, quantity + delta);
        tvQuantity.setText(String.valueOf(quantity));
        updatePriceBasedOnSize();
    }

    private void addToCart() {
        String size = getSelectedSize();
        double price = sizePrices.getOrDefault(size, 0.0);

        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userId = task.getResult().getId();
                CartModel newCartItem = new CartModel(foodId, size, quantity, price, userId);
                cartService.checkAndAddOrUpdateCartItem(newCartItem);

            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
        animateToCart();
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
        String selectedSize = getSelectedSize();
        double price;

        if (selectedSize.isEmpty()) {
            price = sizePrices.getOrDefault("DEFAULT", 0.0);
        } else {
            price = sizePrices.getOrDefault(selectedSize, 0.0);
        }

        double total = price * quantity;
        double priceInVND = total * 1000;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(priceInVND);
        tvPrice.setText(formattedPrice + " đ");
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

        Map<String, Long> rawSizePrices = (Map<String, Long>) doc.get("sizePrices");

        RadioGroup rgSize = findViewById(R.id.rgSize);

        if (rawSizePrices == null || rawSizePrices.isEmpty()) {
            llSizeRow.setVisibility(View.GONE);

            Double price = doc.getDouble("price");
            if (price != null) {
                sizePrices.put("DEFAULT", price);
            }
        } else {
            for (Map.Entry<String, Long> entry : rawSizePrices.entrySet()) {
                sizePrices.put(entry.getKey(), entry.getValue().doubleValue());
            }
            rgSize.setVisibility(View.VISIBLE);
        }

        updatePriceBasedOnSize();

    }

    private void loadShopInfo(Long storeIdLong) {
        if (storeIdLong == null) return;

        int storeId = storeIdLong.intValue();
        shopService.getShopById(storeId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot shopDoc = task.getResult().getDocuments().get(0);
                String shopName = shopDoc.getString("shopName");
                String address = shopDoc.getString("address");
                if (address != null && !address.isEmpty()) {
                    setupTime(address);
                }
                tvStore.setText(shopName != null ? shopName : "Tên cửa hàng");
            } else {
                Toast.makeText(this, "Không tìm thấy cửa hàng", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void loadCartItemsRealtime() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();
        CartService cartService = new CartService();

        cartListener = cartService.listenToCartByUserId(userId, (querySnapshot, e) -> {
            if (e != null) {
                Log.e("CartListener", "Lỗi khi lắng nghe thay đổi giỏ hàng", e);
                return;
            }

            if (querySnapshot != null) {
                int cartItemCount = querySnapshot.size();
                tvCartCount.setText(String.valueOf(cartItemCount));
                tvCartCount.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void animateToCart() {
        ImageView imgFlying = findViewById(R.id.imgFlyingFood);

        // 1. Lấy vị trí trên màn hình
        int[] foodLocation = new int[2];
        imgFood.getLocationOnScreen(foodLocation);

        int[] cartLocation = new int[2];
        imgCart.getLocationOnScreen(cartLocation);

        // 2. Hiển thị ảnh bay tại vị trí món ăn
        imgFlying.setImageDrawable(imgFood.getDrawable());
        imgFlying.setX(foodLocation[0]);
        imgFlying.setY(foodLocation[1]);
        imgFlying.setVisibility(View.VISIBLE);

        // 3. Animation
        imgFlying.animate()
                .x(cartLocation[0])
                .y(cartLocation[1])
                .setDuration(600)
                .withEndAction(() -> imgFlying.setVisibility(View.GONE))
                .start();
    }
    @Override
    protected void onStop() {
        super.onStop();
        detachCartListener();
    }
 
    private void detachCartListener() {
        if (cartListener != null) {
            cartListener.remove();
            cartListener = null;
        }
    }

}
