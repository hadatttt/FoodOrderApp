package com.example.foodorderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class HotFoodAdapter extends RecyclerView.Adapter<HotFoodAdapter.FoodViewHolder> {

    private List<FoodModel> foodList;

    // Constructor
    public HotFoodAdapter(List<FoodModel> foodList) {
        this.foodList = foodList;
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
            imageFood.setImageResource(food.getImageResId());
            textFoodName.setText(food.getName());
            textFoodPrice.setText(food.getPrice());
            textRating.setText(String.valueOf(food.getRating()));
            textSoldAmount.setText("Đã bán: " + food.getSold());
        }
    }
}