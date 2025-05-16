package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.foodorderapp.databinding.SearchItemFoodBinding;
import com.example.foodorderapp.model.FoodModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.FoodViewHolder> {

    private final List<FoodModel> foodList = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(FoodModel item);
    }

    public SearchResultAdapter(OnItemClickListener listener) {
        this.listener = listener;
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
        FoodModel item = foodList.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return foodList.size();
    }

    public void updateResults(List<FoodModel> newResults) {
        foodList.clear();
        if (newResults != null) foodList.addAll(newResults);
        notifyDataSetChanged();
    }

    class FoodViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemFoodBinding binding;

        FoodViewHolder(SearchItemFoodBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FoodModel item) {
            binding.searchFoodName.setText(item.getName());

            String priceText = String.format("%.2f", item.getPrice()) + " ₫";
            binding.searchFoodPrice.setText(priceText);

            Glide.with(binding.searchFoodImage.getContext())
                    .load(item.getImageUrl())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(binding.searchFoodImage);

            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) listener.onItemClick(item);
            });
        }
    }
}
