package com.example.foodorderapp.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.databinding.SearchItemHotFoodBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.ShopService;


import java.util.List;

public class SearchHotFoodAdapter extends RecyclerView.Adapter<SearchHotFoodAdapter.FoodViewHolder> {

    private final List<FoodModel> hotFoods;
    private final OnItemClickListener listener;
    private final ShopService shopService = new ShopService();

    public interface OnItemClickListener {
        void onItemClick(FoodModel item);
    }

    public SearchHotFoodAdapter(List<FoodModel> hotFoods, OnItemClickListener listener) {
        this.hotFoods = hotFoods;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemHotFoodBinding binding = SearchItemHotFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        holder.bind(hotFoods.get(position));
    }

    @Override
    public int getItemCount() {
        return hotFoods.size();
    }

    public void updateItems(List<FoodModel> newItems) {
        hotFoods.clear();
        hotFoods.addAll(newItems);
        notifyDataSetChanged();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemHotFoodBinding binding;

        public FoodViewHolder(@NonNull SearchItemHotFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(hotFoods.get(pos));
                }
            });
        }

        public void bind(FoodModel food) {
            binding.favoriteName.setText(food.getName());

            // Load shop name từ storeId
            shopService.getShopById(food.getStoreId())
                    .addOnSuccessListener(snapshot -> {
                        if (!snapshot.isEmpty()) {
                            ShopModel shop = snapshot.getDocuments().get(0).toObject(ShopModel.class);
                            if (shop != null) {
                                binding.shopName.setText(shop.getShopName());
                            } else {
                                binding.shopName.setText("Unknown shop");
                            }
                        } else {
                            binding.shopName.setText("Unknown shop");
                        }
                    })
                    .addOnFailureListener(e -> {
                        binding.shopName.setText("Unknown shop");
                        Log.e("SearchHotFoodAdapter", "Error loading shop name: " + e.getMessage());
                    });


            // Load ảnh món ăn
            Glide.with(binding.favoriteImage.getContext())
                    .load(food.getImageUrl())
                    .into(binding.favoriteImage);
        }
    }
}
