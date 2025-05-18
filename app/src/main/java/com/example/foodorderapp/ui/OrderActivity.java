package com.example.foodorderapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.foodorderapp.websocket.WebSocketManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvDelivery, rvHistory, rvConfirm;
    private ArrayList<OrderModel> orderList, historyList, confirmList;
    private OrderAdapter adapterDelivery, adapterHistory, adapterConfirm;

    private LinearLayout btnDelivery, btnHistory, btnConfirm;
    private TextView tvDelivery, tvHistory, tvConfirm;
    private View vDelivery, vHistory, vConfirm;
    private OrderService orderService;
    private ImageButton btnBack;

    private String currentTab = "CONFIRM"; // CONFIRM, DELIVERY, HISTORY

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

        orderList = new ArrayList<>();
        historyList = new ArrayList<>();
        confirmList = new ArrayList<>();

        rvDelivery.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));
        rvConfirm.setLayoutManager(new LinearLayoutManager(this));

        adapterDelivery = new OrderAdapter(orderList, this);
        adapterHistory = new OrderAdapter(historyList, this);
        adapterConfirm = new OrderAdapter(confirmList, this);

        rvDelivery.setAdapter(adapterDelivery);
        rvHistory.setAdapter(adapterHistory);
        rvConfirm.setAdapter(adapterConfirm);

        // Mặc định hiển thị tab Confirm
        showConfirmTab();
        reloadOrdersData();

        btnDelivery.setOnClickListener(view -> {
            showDeliveryTab();
            updateCurrentTab();
        });

        btnHistory.setOnClickListener(view -> {
            showHistoryTab();
            updateCurrentTab();
        });

        btnConfirm.setOnClickListener(view -> {
            showConfirmTab();
            updateCurrentTab();
        });

        btnBack.setOnClickListener(view -> finish());

        WebSocketManager.getInstance().setOnOrderUpdateListener(() -> {
            runOnUiThread(() -> {
                reloadOrdersData();
            });
        });
    }

    private void showDeliveryTab() {
        currentTab = "DELIVERY";

        tvDelivery.setTextColor(ContextCompat.getColor(this, R.color.orange));
        vDelivery.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        vDelivery.setVisibility(View.VISIBLE);

        tvHistory.setTextColor(Color.parseColor("#8E8E8E"));
        vHistory.setVisibility(View.GONE);

        tvConfirm.setTextColor(Color.parseColor("#8E8E8E"));
        vConfirm.setVisibility(View.GONE);

        rvDelivery.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.GONE);
        rvConfirm.setVisibility(View.GONE);
    }

    private void showHistoryTab() {
        currentTab = "HISTORY";

        tvHistory.setTextColor(ContextCompat.getColor(this, R.color.orange));
        vHistory.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        vHistory.setVisibility(View.VISIBLE);

        tvDelivery.setTextColor(Color.parseColor("#8E8E8E"));
        vDelivery.setVisibility(View.GONE);

        tvConfirm.setTextColor(Color.parseColor("#8E8E8E"));
        vConfirm.setVisibility(View.GONE);

        rvHistory.setVisibility(View.VISIBLE);
        rvDelivery.setVisibility(View.GONE);
        rvConfirm.setVisibility(View.GONE);
    }

    private void showConfirmTab() {
        currentTab = "CONFIRM";

        tvConfirm.setTextColor(ContextCompat.getColor(this, R.color.orange));
        vConfirm.setBackgroundColor(ContextCompat.getColor(this, R.color.orange));
        vConfirm.setVisibility(View.VISIBLE);

        tvDelivery.setTextColor(Color.parseColor("#8E8E8E"));
        vDelivery.setVisibility(View.GONE);

        tvHistory.setTextColor(Color.parseColor("#8E8E8E"));
        vHistory.setVisibility(View.GONE);

        rvConfirm.setVisibility(View.VISIBLE);
        rvDelivery.setVisibility(View.GONE);
        rvHistory.setVisibility(View.GONE);
    }

    private void updateCurrentTab() {
        switch (currentTab) {
            case "DELIVERY":
                adapterDelivery.notifyDataSetChanged();
                break;
            case "HISTORY":
                adapterHistory.notifyDataSetChanged();
                break;
            case "CONFIRM":
            default:
                adapterConfirm.notifyDataSetChanged();
                break;
        }
    }

    private void reloadOrdersData() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) return;

        String userId = currentUser.getUid();

        orderService.getOrdersByUserId(userId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    historyList.clear();
                    confirmList.clear();

                    for (DocumentSnapshot doc : queryDocumentSnapshots.getDocuments()) {
                        OrderModel order = doc.toObject(OrderModel.class);
                        if (order != null) {
                            String status = order.getStatus();
                            if (status != null && (status.equalsIgnoreCase("Chờ xác nhận") || status.equalsIgnoreCase("Đang hủy"))) {
                                confirmList.add(order);
                            } else if (status != null && status.equalsIgnoreCase("Đang giao")) {
                                orderList.add(order);
                            } else if (status != null &&
                                    (status.equalsIgnoreCase("Hoàn thành") || status.equalsIgnoreCase("Đã hủy"))) {
                                historyList.add(order);
                            }
                        }
                    }

                    Comparator<OrderModel> orderDateComparator = (o1, o2) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                            Date d1 = sdf.parse(o1.getOrderDate());
                            Date d2 = sdf.parse(o2.getOrderDate());
                            return d2.compareTo(d1); // giảm dần
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    };

                    Collections.sort(confirmList, orderDateComparator);
                    Collections.sort(orderList, orderDateComparator);
                    Collections.sort(historyList, orderDateComparator);

                    updateCurrentTab(); // Cập nhật danh sách hiển thị theo tab
                })
                .addOnFailureListener(e -> Log.e("OrderActivity", "Lỗi reload đơn hàng: " + e.getMessage()));
    }


    @Override
    protected void onResume() {
        super.onResume();
        WebSocketManager.getInstance().setOnOrderUpdateListener(() -> {
            runOnUiThread(() -> {

                Log.d("OrderActivity", "WebSocket cập nhật, reload dữ liệu");
                reloadOrdersData();

            });
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        WebSocketManager.getInstance().setOnOrderUpdateListener(null);
    }
}
