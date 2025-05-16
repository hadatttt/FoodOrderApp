package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.ShopOrderAdapter;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.OrderService;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class ShopManager extends AppCompatActivity {
    private static final String TAG = "ShopManager";

    private FoodService foodService = new FoodService();
    private OrderService orderService = new OrderService();

    private RecyclerView recyclerViewOrders;
    private ShopOrderAdapter shopOrderAdapter;
    private List<OrderModel> orderList = new ArrayList<>();

    private TextView tabPending, tabHistory;
    private String selectedTab = "pending"; // mặc định là đơn đang chờ

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_manager);

        recyclerViewOrders = findViewById(R.id.recyclerOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với callback
        shopOrderAdapter = new ShopOrderAdapter(this, new ArrayList<>(), new ShopOrderAdapter.OnOrderStatusUpdatedListener() {
            @Override
            public void onOrderStatusUpdated() {
                // Khi có đơn hàng thay đổi trạng thái, gọi lại filter để cập nhật danh sách hiển thị
                filterOrders();
            }
        });
        recyclerViewOrders.setAdapter(shopOrderAdapter);

        tabPending = findViewById(R.id.tabPending);
        tabHistory = findViewById(R.id.tabHistory);

        // Bắt sự kiện khi bấm tab
        tabPending.setOnClickListener(v -> {
            selectedTab = "pending";
            updateTabUI(tabPending, tabHistory);
            filterOrders();
        });

        tabHistory.setOnClickListener(v -> {
            selectedTab = "done";
            updateTabUI(tabHistory, tabPending);
            filterOrders();
        });

        String shopIdStr = getIntent().getStringExtra("shopid");
        if (shopIdStr == null) {
            Log.e(TAG, "Shop ID is null, finishing activity");
            finish();
            return;
        }

        int shopId;
        try {
            shopId = Integer.parseInt(shopIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid shopId format");
            finish();
            return;
        }

        loadFoodsAndOrders(shopId);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layoutShopManager), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void loadFoodsAndOrders(int shopId) {
        Log.d(TAG, "Loading foods for shopId: " + shopId);
        foodService.getFoodsByStoreId(shopId)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Integer> foodIdList = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        Long foodIdLong = doc.getLong("foodId");
                        if (foodIdLong != null) {
                            foodIdList.add(foodIdLong.intValue());
                        }
                    }

                    if (foodIdList.isEmpty()) {
                        Log.d(TAG, "No foods found for shopId: " + shopId);
                        return;
                    }

                    loadOrdersByFoodIds(foodIdList);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get foods: " + e.getMessage()));
    }

    private void loadOrdersByFoodIds(List<Integer> foodIdList) {
        Log.d(TAG, "Loading orders for food IDs: " + foodIdList);
        orderService.getOrdersByFoodIds(foodIdList)
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    orderList.clear();
                    for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                        OrderModel order = doc.toObject(OrderModel.class);
                        orderList.add(order);
                    }
                    Log.d(TAG, "Orders loaded: " + orderList.size());
                    filterOrders();
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get orders: " + e.getMessage()));
    }

    private void filterOrders() {
        List<OrderModel> filteredList = new ArrayList<>();
        for (OrderModel order : orderList) {
            String status = order.getStatus();
            if (selectedTab.equals("pending") &&
                    ("Đang giao".equalsIgnoreCase(status))) {
                filteredList.add(order);
            } else if (selectedTab.equals("done") &&
                    ("Đã hủy".equalsIgnoreCase(status) || "Hoàn thành".equalsIgnoreCase(status))) {
                filteredList.add(order);
            }
        }
        shopOrderAdapter.setOrders(filteredList);
        // Không cần gọi notifyDataSetChanged ở đây vì setOrders() đã gọi rồi
    }

    private void updateTabUI(TextView selected, TextView unselected) {
        selected.setBackgroundResource(R.drawable.bg_tag_selected);
        selected.setTextColor(getResources().getColor(android.R.color.white));

        unselected.setBackgroundResource(R.drawable.bg_tag_unselected);
        unselected.setTextColor(getResources().getColor(R.color.orange)); // màu cam
    }
}
