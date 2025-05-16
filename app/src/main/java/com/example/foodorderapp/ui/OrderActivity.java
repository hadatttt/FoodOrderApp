package com.example.foodorderapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.adapter.OrderAdapter;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.OrderService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvDelivery, rvHistory, rvConfirm;
    public ArrayList<OrderModel> orderList, historyList, confirmList;
    private OrderAdapter myOrdersAdapter;
    private LinearLayout btnDelivery, btnHistory, btnConfirm;
    private TextView tvDelivery, tvHistory, tvConfirm;
    private View vDelivery, vHistory, vConfirm;
    private OrderService orderService;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        orderService = new OrderService();

        rvDelivery = findViewById(R.id.rv_delivery);
        rvHistory = findViewById(R.id.rv_history);
        rvConfirm = findViewById(R.id.rv_confirm);
        btnDelivery = findViewById(R.id.btn_delivery);
        btnHistory = findViewById(R.id.btn_history);
        btnConfirm = findViewById(R.id.btn_confirm);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvHistory = findViewById(R.id.tv_history);
        tvConfirm = findViewById(R.id.tv_confirm);
        vDelivery = findViewById(R.id.v_delivery);
        vHistory = findViewById(R.id.v_history);
        vConfirm = findViewById(R.id.v_confirm);

        btnBack = findViewById(R.id.btn_back);

        rvDelivery.setVisibility(View.GONE);
        rvHistory.setVisibility(View.GONE);
        rvConfirm.setVisibility(View.VISIBLE);

        orderList = new ArrayList<>();
        historyList = new ArrayList<>();
        confirmList = new ArrayList<>();
        rvDelivery.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvConfirm.setLayoutManager(new LinearLayoutManager(this));

        myOrdersAdapter = new OrderAdapter(confirmList, this);
        rvDelivery.setAdapter(myOrdersAdapter);
        rvHistory.setAdapter(myOrdersAdapter);
        rvConfirm.setAdapter(myOrdersAdapter);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Gọi OrderService để lấy danh sách đơn hàng theo userId
            orderService.getOrdersByUserId(userId)
                    .addOnSuccessListener(queryDocumentSnapshots -> {
                        orderList.clear();
                        historyList.clear();
                        confirmList.clear();

                        for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                            OrderModel order = doc.toObject(OrderModel.class);
                            if (order != null) {
                                String status = order.getStatus();
                                if (status != null && status.equalsIgnoreCase("Chờ xác nhận")) {
                                    confirmList.add(order);
                                } else if (status != null && status.equalsIgnoreCase("Đang giao")) {
                                    orderList.add(order);
                                } else if (status != null &&
                                        (status.equalsIgnoreCase("Hoàn thành") || status.equalsIgnoreCase("Đã hủy"))) {
                                    historyList.add(order);
                                }
                            }
                        }
                        myOrdersAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> Log.e("OrderActivity", "Lỗi lấy đơn hàng: " + e.getMessage()));
        }
        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDelivery.setTextColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vDelivery.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vDelivery.setVisibility(View.VISIBLE);
                tvHistory.setTextColor(Color.parseColor("#8E8E8E"));
                vHistory.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vHistory.setVisibility(View.GONE);
                tvConfirm.setTextColor(Color.parseColor("#8E8E8E"));
                vConfirm.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vConfirm.setVisibility(View.GONE);
                rvDelivery.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                rvConfirm.setVisibility(View.GONE);
                myOrdersAdapter = new OrderAdapter(orderList, OrderActivity.this);
                rvDelivery.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDelivery.setTextColor(Color.parseColor("#8E8E8E"));
                vDelivery.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vDelivery.setVisibility(View.GONE);
                tvHistory.setTextColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vHistory.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vHistory.setVisibility(View.VISIBLE);
                tvConfirm.setTextColor(Color.parseColor("#8E8E8E"));
                vConfirm.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vConfirm.setVisibility(View.GONE);
                rvDelivery.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
                rvConfirm.setVisibility(View.GONE);
                myOrdersAdapter = new OrderAdapter(historyList, OrderActivity.this);
                rvHistory.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDelivery.setTextColor(Color.parseColor("#8E8E8E"));
                vDelivery.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vDelivery.setVisibility(View.GONE);
                tvHistory.setTextColor(Color.parseColor("#8E8E8E"));
                vHistory.setBackgroundColor(Color.parseColor("#8E8E8E"));
                vHistory.setVisibility(View.GONE);
                tvConfirm.setTextColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vConfirm.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vConfirm.setVisibility(View.VISIBLE);
                rvDelivery.setVisibility(View.GONE);
                rvHistory.setVisibility(View.GONE);
                rvConfirm.setVisibility(View.VISIBLE);
                myOrdersAdapter = new OrderAdapter(historyList, OrderActivity.this);
                rvHistory.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();;
            }
        });

    }
}