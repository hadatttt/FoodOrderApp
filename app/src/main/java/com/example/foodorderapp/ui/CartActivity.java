package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.CartAdapter;
import com.example.foodorderapp.model.CartModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import java.util.ArrayList;
import java.util.List;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartModel> cartList;
    private FirebaseFirestore db;
    private TextView tvPrice;
    private ImageButton btnBack;
    private Button btnPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

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
            totalPrice += cartItem.getPrice(); // Cộng dồn giá trị từ các item
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
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();

        // Chuyển giỏ hàng thành các đơn hàng
        List<OrderItem> orderItems = new ArrayList<>();
        double totalPrice = 0;

        for (CartModel cartItem : cartList) {
            OrderItem orderItem = new OrderItem(
                    cartItem.getFoodId(),
                    cartItem.getName(),
                    cartItem.getSize(),
                    cartItem.getQuantity(),
                    cartItem.getPrice()
            );
            orderItems.add(orderItem);
            totalPrice += cartItem.getPrice();
        }

        // Tạo OrderModel
        String orderId = db.collection("orders").document().getId();
        OrderModel order = new OrderModel(
                orderId,
                userId,
                orderItems,
                String.valueOf(System.currentTimeMillis()), // thời gian đơn hàng
                "Processing" // trạng thái đơn hàng
        );

        // Thêm đơn hàng vào Firestore
        db.collection("orders")
                .document(orderId)
                .set(order)
                .addOnSuccessListener(aVoid -> {
                    Log.d("CartActivity", "Đơn hàng đã được thêm thành công");

                    // Xóa tất cả các sản phẩm trong giỏ hàng
                    clearCart(userId);

                    // Chuyển tới màn hình chính
                    Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                    startActivity(intent);
                })
                .addOnFailureListener(e -> Log.e("CartActivity", "Lỗi thêm đơn hàng: " + e.getMessage()));
    }

    private void clearCart(String userId) {
        // Xóa tất cả các sản phẩm trong giỏ hàng của người dùng
        db.collection("carts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        documentSnapshot.getReference().delete();
                    }
                    Log.d("CartActivity", "Giỏ hàng đã được xóa.");
                })
                .addOnFailureListener(e -> Log.e("CartActivity", "Lỗi xóa giỏ hàng: " + e.getMessage()));
    }
}
