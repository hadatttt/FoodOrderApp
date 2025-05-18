package com.example.foodorderapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.OrderService;
import com.example.foodorderapp.ui.OrderActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShopOrderAdapter extends RecyclerView.Adapter<ShopOrderAdapter.OrderViewHolder> {

    public interface OnOrderStatusUpdatedListener {
        void onOrderStatusUpdated();
    }

    private List<OrderModel> orderList;
    private final Context context;
    private final OnOrderStatusUpdatedListener listener;

    public ShopOrderAdapter(Context context, List<OrderModel> orders, OnOrderStatusUpdatedListener listener) {
        this.context = context;
        this.orderList = new ArrayList<>(orders); // tránh thay đổi ngoài ý muốn
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_shop_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        // Lấy thông tin món ăn
        FoodService foodService = new FoodService();
        foodService.getFoodDetails(order.getFoodId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("imageUrl");

                        holder.tvFoodName.setText(name);
                        Glide.with(context)
                                .load(imageUrl)
                                .into(holder.imgFood);
                    }
                });

        // Hiển thị thông tin đơn hàng
        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvFoodQuantity.setText("Số lượng: " + order.getQuantity());
        holder.tvFoodSize.setText("Size: " + order.getSize());

        double priceInVND = order.getPrice() * 1000;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(priceInVND);
        holder.tvFoodPrice.setText("Giá: " + formattedPrice + " đ");

        holder.tvOrderDate.setText(order.getOrderDate());

        // Reset trạng thái nút để tránh tái sử dụng ViewHolder lỗi
        holder.btnMarkComplete.setVisibility(View.VISIBLE);
        holder.btnCancelOrder.setVisibility(View.VISIBLE);
        holder.btnMarkComplete.setEnabled(true);
        holder.btnCancelOrder.setEnabled(true);

        String status = order.getStatus();

        if ("Hoàn thành".equalsIgnoreCase(status)) {
            // Đơn hoàn thành: disable nút Hoàn thành, ẩn nút Hủy
            holder.btnMarkComplete.setText("Hoàn thành");
            holder.btnMarkComplete.setEnabled(false);
            holder.btnCancelOrder.setVisibility(View.GONE);
        } else if ("Đã hủy".equalsIgnoreCase(status)) {
            // Đơn đã hủy: disable nút Hủy, ẩn nút Hoàn thành
            holder.btnCancelOrder.setText("Đã hủy");
            holder.btnCancelOrder.setEnabled(false);
            holder.btnMarkComplete.setVisibility(View.GONE);
        } else {
            // Đơn đang chờ xử lý: hiển thị cả 2 nút, enable cả 2
            holder.btnMarkComplete.setText("Hoàn thành");
            holder.btnMarkComplete.setEnabled(true);
            holder.btnMarkComplete.setVisibility(View.VISIBLE);

            holder.btnCancelOrder.setText("Hủy");
            holder.btnCancelOrder.setEnabled(true);
            holder.btnCancelOrder.setVisibility(View.VISIBLE);

            holder.btnMarkComplete.setOnClickListener(v -> {
                order.setStatus("Hoàn thành");
                updateOrderStatus(order, position);
            });

            holder.btnCancelOrder.setOnClickListener(v -> {
                order.setStatus("Đã hủy");
                updateOrderStatus(order, position);
            });
        }
    }

    private void updateOrderStatus(OrderModel order, int position) {
        OrderService orderService = new OrderService();
        orderService.updateOrder(order.getOrderId(), order)
                .addOnSuccessListener(aVoid -> {
                    orderList.remove(position);
                    notifyItemRemoved(position);
                    notifyDataSetChanged();

                    // Cập nhật thời gian đơn hàng
                    Date date = new Date();
                    SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
                    order.setOrderDate(sdf.format(date));

                    if (context instanceof OrderActivity) {
                        ((OrderActivity) context).historyList.add(order);
                    }

                    // Gọi callback báo Activity cập nhật lại UI hoặc tải lại danh sách
                    if (listener != null) {
                        listener.onOrderStatusUpdated();
                    }
                })
                .addOnFailureListener(e -> Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage()));
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvFoodName, tvFoodQuantity, tvFoodSize, tvFoodPrice, tvOrderDate;
        ImageView imgFood;
        Button btnMarkComplete, btnCancelOrder;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvFoodName = itemView.findViewById(R.id.tv_food_name);
            tvFoodQuantity = itemView.findViewById(R.id.tv_food_quantity);
            tvFoodSize = itemView.findViewById(R.id.tv_food_size);
            tvFoodPrice = itemView.findViewById(R.id.tv_food_price);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            imgFood = itemView.findViewById(R.id.img_food);
            btnMarkComplete = itemView.findViewById(R.id.btn_mark_complete);
            btnCancelOrder = itemView.findViewById(R.id.btn_cancel_order);
        }
    }

    // Thêm đơn hàng
    public void addOrder(OrderModel order) {
        orderList.add(order);
        notifyItemInserted(orderList.size() - 1);
    }

    // Cập nhật toàn bộ danh sách đơn hàng
    public void setOrders(List<OrderModel> newOrders) {
        this.orderList = new ArrayList<>(newOrders);
        notifyDataSetChanged();
    }
}
