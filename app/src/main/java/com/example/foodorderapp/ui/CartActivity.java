package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.CartAdapter;
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.OrderService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartModel> cartList;
    private FirebaseFirestore db;
    private TextView tvPrice;
    private ImageButton btnBack;
    private Button btnPay;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tvPrice = findViewById(R.id.tvPrice);
        recyclerView = findViewById(R.id.rv_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btn_back);
        btnPay = findViewById(R.id.btn_pay);

        db = FirebaseFirestore.getInstance();
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList);
        recyclerView.setAdapter(cartAdapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                processOrder();
            }
        });
    }

    private void loadCartItems(String userId) {
        db.collection("carts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            CartModel cartItem = documentSnapshot.toObject(CartModel.class);
                            cartList.add(cartItem);
                        }
                        cartAdapter.notifyDataSetChanged();
                        updateTotalPrice();
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CartActivity", "Lỗi lấy dữ liệu giỏ hàng: " + e.getMessage());
                });
    }
    public void updateTotalPrice() {
        double totalPrice = 0;
        for (CartModel cartItem : cartList) {
            totalPrice += cartItem.getPrice();
        }
        tvPrice.setText(String.format("%.3fđ", totalPrice));
    }
    @Override
    protected void onResume() {
        super.onResume();
        cartList.clear();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        loadCartItems(userId);
    }
    private void processOrder() {
        final int[] d = {0};
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            OrderService orderService = new OrderService();
            OrderModel order = new OrderModel();
            for (CartModel cartItem : cartList) {

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                order = new OrderModel(
                        userId,
                        cartItem.getFoodId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice(),
                        sdf.format(date),
                        "Đang giao"
                );
                orderService.addOrder(order).addOnSuccessListener(aVoid -> {
                    d[0]++;
                    if (d[0] == cartList.size()) {

                        progressBar.setVisibility(View.GONE);
                        Log.d("CartActivity", "Tất cả đơn hàng đã được thêm");

                         clearCart(userId);

                        Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("CartActivity", "Lỗi thêm đơn hàng: " );
                    progressBar.setVisibility(View.GONE);
                });
            }

        }
    }



    private void clearCart(String userId) {
        CartService cartService = new CartService();
        cartService.clearCartByUserId(userId);
    }
}
