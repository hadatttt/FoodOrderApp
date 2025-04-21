package com.example.foodorderapp;

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

import java.util.ArrayList;
import java.util.List;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {
    private ArrayList<MyOrders> orderList;
    private Context context;
    public MyOrdersAdapter(ArrayList<MyOrders> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyOrdersAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_food, parent, false);

        return new MyOrdersAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyOrdersAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(orderList.get(position).getName());
        holder.tvTitle.setText(orderList.get(position).getTitle());
        holder.tvId.setText("#"+orderList.get(position).getId());
        holder.tvPrice.setText(orderList.get(position).getPrice() + "đ");
        holder.tvQuantity.setText(orderList.get(position).getQuantity()+ " món");
        holder.tvStatus.setText(orderList.get(position).getStatus());
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
            holder.tvDate.setText(orderList.get(position).getDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        } else  if (orderList.get(position).getStatus().equals("Đã hủy")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.tvDate.setText(orderList.get(position).getDate());
            holder.tvDate.setVisibility(View.VISIBLE);
        }
        int imageResId = context.getResources().getIdentifier(orderList.get(position).getImage(), "drawable", context.getPackageName());
        holder.image.setImageResource(imageResId);
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

            tvName = view.findViewById(R.id.tv_name_food);
            tvTitle = view.findViewById(R.id.tv_title);
            tvPrice = view.findViewById(R.id.tv_price);
            tvQuantity = view.findViewById(R.id.tv_quantity);
            tvId = view.findViewById(R.id.tv_id);
            tvDate = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.img_food);
            tvStatus = view.findViewById(R.id.tv_status);
            btnFollow = view.findViewById(R.id.btn_follow);
            btnCancel = view.findViewById(R.id.btn_cancel);
            btnFeedback = view.findViewById(R.id.btn_feedback);
            btnRepurchase = view.findViewById(R.id.btn_repurchase);
        }
    }
}
