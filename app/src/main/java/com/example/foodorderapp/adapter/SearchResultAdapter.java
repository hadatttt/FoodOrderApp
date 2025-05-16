package com.example.foodorderapp.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodorderapp.databinding.SearchItemFoodBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.SearchResultModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.ShopService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.FoodViewHolder> {
    private final List<SearchResultModel> resultList = new ArrayList<>();

    private final OnItemClickListener listener;
    private ShopService shopService;
    public interface OnItemClickListener {
        void onFoodClick(FoodModel item);
        void onShopClick(ShopModel shop);
    }

    public SearchResultAdapter(OnItemClickListener listener) {
        this.listener = listener;
        this.shopService = new ShopService();
    }

    @NonNull
    @Override
    public FoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemFoodBinding binding = SearchItemFoodBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodViewHolder holder, int position) {
        SearchResultModel item = resultList.get(position);
        holder.bind(item);
    }


    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public void updateResults(List<SearchResultModel> newResults) {
        resultList.clear();
        if (newResults != null) resultList.addAll(newResults);
        notifyDataSetChanged();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemFoodBinding binding;

        FoodViewHolder(SearchItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(SearchResultModel result) {
            if ("food".equals(result.getType()) && result.getFood() != null) {
                FoodModel food = result.getFood();
                binding.searchFoodName.setText(food.getName());
                Glide.with(binding.searchFoodImage.getContext())
                        .load(food.getImageUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.searchFoodImage);

                binding.searchFoodPrice.setTextColor(Color.RED);
                String priceText = String.format("%.3f", food.getPrice()) + " ₫";
                binding.searchFoodPrice.setText(priceText);

                binding.getRoot().setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onFoodClick(food);
                    }
                });

            } else if ("shop".equals(result.getType()) && result.getShop() != null) {
                ShopModel shop = result.getShop();
                binding.searchFoodName.setText(shop.getShopName());
                Glide.with(binding.searchFoodImage.getContext())
                        .load(shop.getImageUrl())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.searchFoodImage);

                double rating = shop.getRating();
                binding.searchFoodPrice.setTextColor(Color.parseColor("#FF9800"));
                binding.searchFoodPrice.setText("★ " + String.format("%.1f", rating));

                binding.getRoot().setOnClickListener(v -> {
                    if (listener != null) {
                        listener.onShopClick(shop);
                    }
                });
            }
        }

    }
}
