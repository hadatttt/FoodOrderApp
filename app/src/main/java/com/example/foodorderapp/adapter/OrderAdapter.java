package com.example.foodorderapp.adapter;

import android.content.Context;
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
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<OrderModel> orderList;
    private Context context;
    public OrderAdapter(ArrayList<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.activity_detail, parent, false);

        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        FoodService foodService = new FoodService();
        // Gọi FoodService để lấy thông tin món ăn
        foodService.getFoodDetails(order.getFoodId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("imageUrl");

                        holder.tvName.setText(name);

                        Glide.with(context)
                                .load(imageUrl)
//                                .placeholder(R.drawable.loading)          // Ảnh tạm trong lúc load
//                                .error(R.drawable.default_image)          // Ảnh lỗi nếu load fail
                                .into(holder.image);
                    }
                });
        holder.tvId.setText("#"+order.getOrderId());
        holder.tvPrice.setText(order.getPrice() + "đ");
        holder.tvQuantity.setText(order.getQuantity()+ " món");
        holder.tvStatus.setText(order.getStatus());
        holder.tvDate.setVisibility(View.GONE);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.btnCancel.setVisibility(View.VISIBLE);
        holder.tvStatus.setVisibility(View.GONE);
        if (orderList.get(position).getStatus().equals("Hoàn thành")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvDate.setText(orderList.get(position).getOrderDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        } else  if (orderList.get(position).getStatus().equals("Đã hủy")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.tvDate.setText(orderList.get(position).getOrderDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvTitle, tvPrice, tvQuantity, tvId, tvStatus, tvDate;
        public ImageView image;
        public Button btnFollow, btnCancel, btnFeedback, btnRepurchase;

        public ViewHolder(View view) {
            super(view);

//            tvName = view.findViewById(R.id.tvFoodName);
//            tvTitle = view.findViewById(R.id.tv_title);
//            tvPrice = view.findViewById(R.id.tv_price);
//            tvQuantity = view.findViewById(R.id.tv_quantity);
//            tvId = view.findViewById(R.id.tv_id);
//            tvDate = view.findViewById(R.id.tv_date);
//            image = view.findViewById(R.id.img_food);
//            tvStatus = view.findViewById(R.id.tv_status);
//            btnFollow = view.findViewById(R.id.btn_follow);
//            btnCancel = view.findViewById(R.id.btn_cancel);
//            btnFeedback = view.findViewById(R.id.btn_feedback);
//            btnRepurchase = view.findViewById(R.id.btn_repurchase);
        }
    }
}
