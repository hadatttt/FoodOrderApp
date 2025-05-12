package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.adapter.OrderAdapter;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.OrderModel;

import java.util.ArrayList;

public class OrderActivity extends AppCompatActivity {
    private RecyclerView rvDelivery, rvHistory;
    private ArrayList<OrderModel> orderList, historyList;
    private OrderAdapter myOrdersAdapter;
    private LinearLayout btnDelivery, btnHistory;
    private TextView tvDelivery, tvHistory;
    private View vDelivery, vHistory;
    private ViewGroup.LayoutParams params;
    private int widthInPx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);
        rvDelivery = findViewById(R.id.rv_delivery);
        rvHistory = findViewById(R.id.rv_history);
        btnDelivery = findViewById(R.id.btn_delivery);
        btnHistory = findViewById(R.id.btn_history);
        tvDelivery = findViewById(R.id.tv_delivery);
        tvHistory = findViewById(R.id.tv_history);
        vDelivery = findViewById(R.id.v_delivery);
        vHistory = findViewById(R.id.v_history);

        rvDelivery.setVisibility(View.VISIBLE);
        rvHistory.setVisibility(View.GONE);


        orderList = new ArrayList<>();
        historyList = new ArrayList<>();
        rvDelivery.setLayoutManager(new LinearLayoutManager(this));
        rvHistory.setLayoutManager(new LinearLayoutManager(this));

        myOrdersAdapter = new OrderAdapter(orderList, this);
        rvDelivery.setAdapter(myOrdersAdapter);
        rvHistory.setAdapter(myOrdersAdapter);

//        orderList.add(new MyOrders(1, "Đồ ăn", "burger", "Pizza Hut", "100.000", 1, "Đang giao", ""));
//        orderList.add(new MyOrders(2, "Đồ ăn", "burger", "Pizza Hut", "75.000", 2,"Đang giao", ""));
//        orderList.add(new MyOrders(3, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Đang giao", ""));
//        historyList.add(new MyOrders(4, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Hoàn thành", "21/04, 11:30"));
//        historyList.add(new MyOrders(5, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Hoàn thành", "21/04, 11:30"));
//        historyList.add(new MyOrders(6, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Hoàn thành", "21/04, 11:30"));
//        historyList.add(new MyOrders(7, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Đã hủy", "21/04, 11:30"));
//        historyList.add(new MyOrders(8, "Đồ ăn", "burger", "Pizza Hut", "89.000", 3,"Đã hủy", "21/04, 11:30"));

        myOrdersAdapter.notifyDataSetChanged();
        btnDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDelivery.setTextColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vDelivery.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                tvHistory.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
                vHistory.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.gray));
                rvDelivery.setVisibility(View.VISIBLE);
                rvHistory.setVisibility(View.GONE);
                myOrdersAdapter = new OrderAdapter(orderList, OrderActivity.this);
                rvDelivery.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();
            }
        });
        btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvDelivery.setTextColor(ContextCompat.getColor(view.getContext(), R.color.gray));
                vDelivery.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.gray));
                tvHistory.setTextColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                vHistory.setBackgroundColor(ContextCompat.getColor(view.getContext(), R.color.orange));
                rvDelivery.setVisibility(View.GONE);
                rvHistory.setVisibility(View.VISIBLE);
                myOrdersAdapter = new OrderAdapter(historyList, OrderActivity.this);
                rvHistory.setAdapter(myOrdersAdapter);
                myOrdersAdapter.notifyDataSetChanged();
            }
        });


    }
}