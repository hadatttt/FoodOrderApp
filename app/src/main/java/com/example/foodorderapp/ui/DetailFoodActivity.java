package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
//import com.example.foodorderapp.databinding.ItemFoodBinding;
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class DetailFoodActivity extends AppCompatActivity {
//    private ItemFoodBinding binding;
//    public int quantity = 1;
//    public double basePrice;
//    public FoodModel currentFood;
//    public int foodId;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = ItemFoodBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        foodId = getIntent().getIntExtra("FOOD_ID", -1);
//        if (foodId != -1) {
//            // Gọi Firebase hoặc một dịch vụ để lấy thông tin món ăn theo foodId
//            getFoodDetails(foodId);
//        } else {
//            // Nếu foodId không hợp lệ, có thể thông báo lỗi hoặc xử lý khác
//            Toast.makeText(this, "Food ID không hợp lệ", Toast.LENGTH_SHORT).show();
//        }
//
//        binding.btnPlus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                quantity++;
//                binding.tvQuantity.setText(String.valueOf(quantity));
//                updatePrice();
//            }
//        });
//        binding.btnMinus.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                quantity--;
//                binding.tvQuantity.setText(String.valueOf(quantity));
//                updatePrice();
//            }
//        });
//        binding.btnBack.setOnClickListener(v -> {
//            finish(); // kết thúc activity hiện tại, quay lại activity trước
//        });
//
//        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addFoodToCart();
//            }
//        });
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
//            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
//            return insets;
//        });
//    }
//    public void updatePrice(){
//        double totalPrice = quantity * basePrice * 1000;
//        DecimalFormat formatter = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.getDefault()));
//        String formattedPrice = formatter.format(totalPrice);
//        binding.tvPrice.setText(formattedPrice + "đ");
//    }
//    private void getFoodDetails(int foodId) {
//        // Giả sử bạn có một FoodService để truy vấn Firebase
//        FoodService foodService = new FoodService();
//
//        // Sử dụng foodId để lấy thông tin món ăn từ Firebase
//        foodService.getFoodDetails(foodId).addOnCompleteListener(task -> {
//            if (task.isSuccessful() && task.getResult() != null) {
//                // Lấy thông tin món ăn từ kết quả trả về
//                DocumentSnapshot document = task.getResult().getDocuments().get(0);
//                FoodModel currentFood = document.toObject(FoodModel.class);
//
//                if (currentFood != null) {
//                    binding.tvFoodName.setText(currentFood.getName());
//                    Glide.with(binding.getRoot())
//                            .load(currentFood.getImageUrl()) // Sử dụng Glide để tải ảnh
//                            .into(binding.imgFood);
//                    basePrice = currentFood.getPrice();
//                    binding.tvRate.setText(String.valueOf(currentFood.getRating()));
//                    updatePrice();
//                }
//            } else {
//                Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//    public void addFoodToCart(){
//        try{
//            int rdId = binding.rgSize.getCheckedRadioButtonId();
//            RadioButton rd = findViewById(rdId);
//            String size = rd.getText().toString();
//            String priceStr = binding.tvPrice.getText().toString();
//            priceStr = priceStr.replace(",", "").replace("đ", "").trim();
//            Double price = Double.parseDouble(priceStr);
//            FirebaseAuth mAuth = FirebaseAuth.getInstance();
//            String userId = mAuth.getCurrentUser().getUid();
//            CartModel cart = new CartModel(foodId, size, quantity, price, userId);
//            CartService cartService = new CartService();
//            cartService.addToCart(cart)
//                    .addOnSuccessListener(documentReference -> {
//                        Log.d("CartService", "Thêm thành công, ID: " + documentReference.getId());
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.e("CartService", "Lỗi thêm item: ", e);
//                    });
//        }catch (Exception e){
//            Log.d("Loi", e.getMessage());
//        }

}

