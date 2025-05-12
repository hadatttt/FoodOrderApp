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

public class HotFoodAdapter extends RecyclerView.Adapter<HotFoodAdapter.FoodViewHolder> {

    private List<FoodModel> foodList;
    private final Context context;

    // Constructor
    public HotFoodAdapter(Context context, List<FoodModel> foodList) {
        this.context = context;
        this.foodList = foodList;
    }

    public void updateData(List<FoodModel> newList) {
        foodList.clear();
        foodList.addAll(newList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate layout item_food.xml manually
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hot, parent, false);
        return new FoodViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        FoodModel food = foodList.get(position);
        holder.bind(food);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public static class FoodViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imageFood;
        private final TextView textFoodName;
        private final TextView textFoodPrice;
        private final TextView textRating;
        private final TextView textSoldAmount;

        public FoodViewHolder(@NonNull View itemView) {
            super(itemView);
            // Initialize views using findViewById
            imageFood = itemView.findViewById(R.id.imageFood);
            textFoodName = itemView.findViewById(R.id.textFoodName);
            textFoodPrice = itemView.findViewById(R.id.textFoodPrice);
            textRating = itemView.findViewById(R.id.textRating);
            textSoldAmount = itemView.findViewById(R.id.textSoldAmount);
        }
        public void bind(FoodModel food) {
            // Tải ảnh từ URL vào ImageView
            Glide.with(itemView.getContext())
                    .load(food.getImageUrl())
                    .into(imageFood);

            // Bắt sự kiện click để mở DetailFoodActivity và truyền foodId
            itemView.setOnClickListener(v -> {
                Context context = itemView.getContext();
                Intent intent = new Intent(context, DetailFoodActivity.class);
                intent.putExtra("foodid", food.getFoodId());  // Đặt tên key rõ ràng hơn
                context.startActivity(intent);
            });

            // Gán dữ liệu vào các view
            textFoodName.setText(food.getName());
            textFoodPrice.setText(String.format("%,.3fđ", food.getPrice()));  // Định dạng giá đẹp hơn
            textRating.setText(String.valueOf(food.getRating()));
            textSoldAmount.setText("Đã bán: " + food.getSold());
        }

    }
}