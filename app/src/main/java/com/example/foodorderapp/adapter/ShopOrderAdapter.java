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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ShopOrderAdapter extends RecyclerView.Adapter<ShopOrderAdapter.OrderViewHolder> {

    private final List<OrderModel> orderList;
    private final Context context;

    public ShopOrderAdapter(Context context, List<OrderModel> orders) {
        this.context = context;
        this.orderList = orders;
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

        // Gọi FoodService để lấy tên và ảnh món ăn dựa trên foodId
        FoodService foodService = new FoodService();
        foodService.getFoodDetails(order.getFoodId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("imageUrl");

                        // Hiển thị tên và ảnh món ăn
                        holder.tvFoodName.setText(name);
                        Glide.with(context)
                                .load(imageUrl)
                                .into(holder.imgFood);
                    }
                });

        // Gán các thông tin đơn hàng
        holder.tvOrderId.setText("Order ID: " + order.getOrderId());
        holder.tvFoodQuantity.setText("Số lượng: " + order.getQuantity());
        holder.tvFoodSize.setText("Size: " + order.getSize());

        // Định dạng giá tiền: double * 1000 → chuỗi "xxx.xxx đ"
        double priceInVND = order.getPrice() * 1000;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(priceInVND);
        holder.tvFoodPrice.setText("Giá: " + formattedPrice + " đ");

        holder.tvOrderDate.setText(order.getOrderDate());

        // Xử lý sự kiện nút "Hoàn thành"
        holder.btnMarkComplete.setOnClickListener(v -> {
            OrderService orderService = new OrderService();
            order.setStatus("Hoàn thành");
            orderService.updateOrder(order.getOrderId(), order)
                    .addOnSuccessListener(aVoid -> {
                        orderList.remove(position);

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        order.setOrderDate(sdf.format(date));

                        ((OrderActivity) context).historyList.add(order);

                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage());
                    });
        });

        // Xử lý sự kiện nút "Hủy"
        holder.btnCancelOrder.setOnClickListener(v -> {
            OrderService orderService = new OrderService();
            order.setStatus("Đã hủy");
            orderService.updateOrder(order.getOrderId(), order)
                    .addOnSuccessListener(aVoid -> {
                        orderList.remove(position);

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        order.setOrderDate(sdf.format(date));

                        ((OrderActivity) context).historyList.add(order);

                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage());
                    });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    // ViewHolder ánh xạ layout item_shop_order.xml
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

    // Hàm thêm đơn hàng mới vào danh sách
    public void addOrder(OrderModel order) {
        orderList.add(order);
        notifyItemInserted(orderList.size() - 1);
    }
}
