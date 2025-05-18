package com.example.foodorderapp.adapter;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.OrderService;
import com.example.foodorderapp.ui.OrderActivity;
import com.example.foodorderapp.websocket.WebSocketManager;
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

        String prefix = "Trạng thái: ";
        String status = order.getStatus();

        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(prefix);

        // Thêm status với màu khác
        int color = ContextCompat.getColor(holder.itemView.getContext(), android.R.color.black);
        if (status.equals("Chờ xác nhận")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        } else if (status.equals("Đang hủy")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.red);
        } else if (status.equals("Đang giao")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        } else if (status.equals("Đã hủy")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.red);
        } else if (status.equals("Hoàn thành")) {
            color = ContextCompat.getColor(holder.itemView.getContext(), R.color.green);
        }

        int start = ssb.length();
        ssb.append(status);
        int end = ssb.length();

        // Áp màu cho phần status
        ssb.setSpan(new ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Gán vào TextView
        holder.tvStatus.setText(ssb);
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

        if ("Chờ xác nhận".equalsIgnoreCase(status) || "Đang hủy".equalsIgnoreCase(status)) {
            holder.btnConfirm.setText("Xác nhận");
            holder.btnConfirm.setEnabled(true);
            holder.btnConfirm.setVisibility(View.VISIBLE);

            holder.btnConfirm.setOnClickListener(v -> {
                order.setStatus("Đang giao");
                updateOrderStatus(order, position);
                WebSocketManager.getInstance().sendAccept("delivery", order.getUserId(), order.getOrderId(), "đang trên đường giao");
            });

            holder.btnCancelOrder.setText("Hủy");
            holder.btnCancelOrder.setEnabled(true);
            holder.btnCancelOrder.setVisibility(View.VISIBLE);

            holder.btnCancelOrder.setOnClickListener(v -> {
                order.setStatus("Đã hủy");
                updateOrderStatus(order, position);
                WebSocketManager.getInstance().sendAccept("cancelled", order.getUserId(), order.getOrderId(), "đã bị hủy");
            });

            holder.btnMarkComplete.setVisibility(View.GONE);
        } else if ("Đã hủy".equalsIgnoreCase(status)) {
            holder.btnCancelOrder.setText("Đã hủy");
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancelOrder.setVisibility(View.VISIBLE);
            holder.btnCancelOrder.setEnabled(false);
            holder.btnMarkComplete.setVisibility(View.GONE);
        }  else if ("Hoàn thành".equalsIgnoreCase(status)) {
            holder.btnConfirm.setText("Hoàn thành");
            holder.btnConfirm.setVisibility(View.VISIBLE);
            holder.btnConfirm.setEnabled(false);
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnMarkComplete.setVisibility(View.GONE);
        } else  if ("Đang giao".equalsIgnoreCase(status)) {
            holder.btnConfirm.setVisibility(View.GONE);
            holder.btnCancelOrder.setVisibility(View.GONE);
            holder.btnMarkComplete.setText("Hoàn thành");
            holder.btnMarkComplete.setEnabled(true);
            holder.btnMarkComplete.setVisibility(View.VISIBLE);
            holder.btnMarkComplete.setOnClickListener(v -> {
                order.setStatus("Hoàn thành");
                updateOrderStatus(order, position);
                WebSocketManager.getInstance().sendAccept("complete", order.getUserId(), order.getOrderId(), "đã hoàn thành");
            });
        }

    }

    private void updateOrderStatus(OrderModel order, int position) {
        if (order.getStatus().equals("Hoàn thành") || order.getStatus().equals("Đã hủy")) {
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
            order.setOrderDate(sdf.format(date));
        }
        OrderService orderService = new OrderService();
        orderService.updateOrder(order.getOrderId(), order)
                .addOnSuccessListener(aVoid -> {
                    if (order.getStatus().equals("Hoàn thành") || order.getStatus().equals("Đã hủy")) {
                        orderList.remove(position);
                        notifyItemRemoved(position);
                        notifyDataSetChanged();
//                        if (order.getStatus().equals("Đã hủy")) {
//                            int storeId = order.get(); // giả sử bạn có field này
//                            String orderId = order.getOrderId();
//
//                            WebSocketManager.getInstance().sendCancelOrder(storeId, orderId, reason);
//                        }
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
        TextView tvOrderId, tvFoodName, tvFoodQuantity, tvFoodSize, tvFoodPrice, tvOrderDate, tvStatus;
        ImageView imgFood;
        Button btnMarkComplete, btnCancelOrder, btnConfirm;

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
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            tvStatus = itemView.findViewById(R.id.tv_status);
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
