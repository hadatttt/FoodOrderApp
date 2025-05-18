package com.example.foodorderapp.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.ui.DetailFoodActivity;

import java.util.List;

public class FavouriteAdapter extends RecyclerView.Adapter<FavouriteAdapter.FavouriteViewHolder> {
    private List<FoodModel> foodList;
    private Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onRemoveClick(FoodModel food);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public FavouriteAdapter(Context context, List<FoodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    @NonNull
    @Override
    public FavouriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_favourite, parent, false);
        return new FavouriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavouriteViewHolder holder, int position) {
        FoodModel food = foodList.get(position);
        holder.tvFoodName.setText(food.getName());

        // Load ảnh vào ImageView (dùng thư viện Glide hoặc Picasso)
        Glide.with(context)
                .load(food.getImageUrl())
                .placeholder(R.drawable.chicken) // ảnh tạm
                .into(holder.imgFood);
        holder.btnRemove.setOnClickListener(v -> {
            if (listener != null) {
                listener.onRemoveClick(food);
            }
        });
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FavouriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imgFood;
        TextView tvFoodName;
        ImageView btnRemove;

        public FavouriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imgFood = itemView.findViewById(R.id.imgFood);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
        public void bind(FoodModel food){
            // Bắt sự kiện click để mở DetailFoodActivity và truyền foodId
            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, DetailFoodActivity.class);
                intent.putExtra("foodId", food.getFoodId());  // Đặt tên key rõ ràng hơn
                context.startActivity(intent);
            });

        }
    }
}
