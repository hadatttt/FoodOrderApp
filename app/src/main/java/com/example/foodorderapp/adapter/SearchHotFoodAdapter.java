package com.example.foodorderapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.databinding.SearchItemHotFoodBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FoodService;

import java.util.List;

public class SearchHotFoodAdapter extends RecyclerView.Adapter<SearchHotFoodAdapter.FavoriteViewHolder> {

    private final List<FoodModel> favoriteItems;
    private final OnItemClickListener listener;
    private final FoodService foodService = new FoodService();

    public interface OnItemClickListener {
        void onItemClick(FoodModel item);
    }

    public SearchHotFoodAdapter(List<FoodModel> favoriteItems, OnItemClickListener listener) {
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemHotFoodBinding binding = SearchItemHotFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        FoodModel item = favoriteItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public void updateItems(List<FoodModel> newFavorites) {
        favoriteItems.clear();
        favoriteItems.addAll(newFavorites);
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemHotFoodBinding binding;

        public FavoriteViewHolder(@NonNull SearchItemHotFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(favoriteItems.get(position));
                }
            });
        }

        public void bind(FoodModel item) {
            binding.favoriteName.setText(item.getName());

            // Gọi bất đồng bộ đến Firestore
            foodService.getFoodsByStoreId(item.getStoreId())
                    .addOnSuccessListener(snapshot -> {
                        List<FoodModel> foodList = snapshot.toObjects(FoodModel.class);
                        if (foodList != null && !foodList.isEmpty()) {
                            binding.shopName.setText(foodList.get(0).getName());
                        } else {
                            binding.shopName.setText("Unknown shop");
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.shopName.setText("Unknown shop");
                        Log.e("SearchHotFoodAdapter", "Error loading shop name: " + e.getMessage());
                    });

            // Load ảnh
            Glide.with(binding.favoriteImage.getContext())
                    .load(item.getImageUrl())
                    .into(binding.favoriteImage);
        }
    }
}
