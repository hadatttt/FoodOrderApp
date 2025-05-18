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
import com.example.foodorderapp.websocket.WebSocketManager;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShopManager extends AppCompatActivity {
    private static final String TAG = "ShopManager";

    private FoodService foodService = new FoodService();
    private OrderService orderService = new OrderService();

    private RecyclerView recyclerViewOrders;
    private ShopOrderAdapter shopOrderAdapter;
    private List<OrderModel> orderList = new ArrayList<>();

    private TextView tabPending, tabHistory, tabConfirm;
    private String selectedTab = "confirm";

    private int shopId = -1; // lưu shopId để tái sử dụng khi load lại dữ liệu

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_shop_manager);

        WebSocketManager.getInstance().setOnCancelRequestListener(() -> {
            runOnUiThread(() -> {
                String shopIdStr = getIntent().getStringExtra("shopid");
                if (shopIdStr != null) {
                    try {
                        int id = Integer.parseInt(shopIdStr);
                        loadFoodsAndOrders(id);  // Reload lại danh sách đơn
                    } catch (NumberFormatException e) {
                        Log.e(TAG, "Invalid shopId format in WebSocket callback");
                    }
                }
            });
        });

        recyclerViewOrders = findViewById(R.id.recyclerOrders);
        recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));

        // Khởi tạo adapter với callback
        shopOrderAdapter = new ShopOrderAdapter(this, new ArrayList<>(), () -> {
            // Khi có đơn hàng thay đổi trạng thái, gọi lại filter để cập nhật danh sách hiển thị
            filterOrders();
        });
        recyclerViewOrders.setAdapter(shopOrderAdapter);

        tabPending = findViewById(R.id.tabPending);
        tabHistory = findViewById(R.id.tabHistory);
        tabConfirm = findViewById(R.id.tabConfirm);

        // Lấy shopId từ Intent và lưu vào biến toàn cục
        String shopIdStr = getIntent().getStringExtra("shopid");
        if (shopIdStr == null) {
            Log.e(TAG, "Shop ID is null, finishing activity");
            finish();
            return;
        }

        try {
            shopId = Integer.parseInt(shopIdStr);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid shopId format");
            finish();
            return;
        }
        Log.d(TAG, "Received shopId: " + shopIdStr);

        // Set sự kiện click cho các tab, khi click thì load lại dữ liệu mới từ Firestore
        tabPending.setOnClickListener(v -> {
            selectedTab = "pending";
            updateTabUI(tabPending, tabConfirm, tabHistory);
            if (shopId != -1) {
                loadFoodsAndOrders(shopId);  // Tải lại dữ liệu
            }
        });

        tabConfirm.setOnClickListener(v -> {
            selectedTab = "confirm";
            updateTabUI(tabConfirm, tabPending, tabHistory);
            if (shopId != -1) {
                loadFoodsAndOrders(shopId);
            }
        });

        tabHistory.setOnClickListener(v -> {
            selectedTab = "done";
            updateTabUI(tabHistory, tabPending, tabConfirm);
            if (shopId != -1) {
                loadFoodsAndOrders(shopId);
            }
        });

        // Lần đầu tiên vào load dữ liệu
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
                        orderList.clear();
                        shopOrderAdapter.setOrders(new ArrayList<>()); // Clear danh sách đơn khi ko có món
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

                    // Sắp xếp orderList theo orderDate giảm dần
                    Collections.sort(orderList, (o1, o2) -> {
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                            Date d1 = sdf.parse(o1.getOrderDate());
                            Date d2 = sdf.parse(o2.getOrderDate());
                            return d2.compareTo(d1); // Sắp xếp giảm dần
                        } catch (ParseException e) {
                            e.printStackTrace();
                            return 0;
                        }
                    });

                    Log.d(TAG, "Orders loaded and sorted: " + orderList.size());
                    filterOrders(); // Lọc và hiển thị theo tab hiện tại
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get orders: " + e.getMessage());
                });
    }

    private void filterOrders() {
        List<OrderModel> filteredList = new ArrayList<>();
        for (OrderModel order : orderList) {
            String status = order.getStatus();
            if (selectedTab.equals("confirm") && ("Chờ xác nhận".equalsIgnoreCase(status) || "Đang hủy".equalsIgnoreCase(status))) {
                filteredList.add(order);
            } else if (selectedTab.equals("pending") && ("Đang giao".equalsIgnoreCase(status))) {
                filteredList.add(order);
            } else if (selectedTab.equals("done") && ("Đã hủy".equalsIgnoreCase(status) || "Hoàn thành".equalsIgnoreCase(status))) {
                filteredList.add(order);
            }
        }
        shopOrderAdapter.setOrders(filteredList);
        // Không cần gọi notifyDataSetChanged ở đây vì setOrders() đã gọi rồi
    }

    private void updateTabUI(TextView selected, TextView unselected1, TextView unselected2) {
        selected.setBackgroundResource(R.drawable.bg_tag_selected);
        selected.setTextColor(getResources().getColor(android.R.color.white));

        unselected1.setBackgroundResource(R.drawable.bg_tag_unselected);
        unselected1.setTextColor(getResources().getColor(R.color.orange)); // màu cam

        unselected2.setBackgroundResource(R.drawable.bg_tag_unselected);
        unselected2.setTextColor(getResources().getColor(R.color.orange)); // màu cam
    }
}
