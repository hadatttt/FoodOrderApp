package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.databinding.SearchItemHotShopBinding;
import com.example.foodorderapp.model.ShopModel;

import java.util.List;

public class SearchHotShopAdapter extends RecyclerView.Adapter<SearchHotShopAdapter.ViewHolder> {

    private final List<ShopModel> shops;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ShopModel item);
    }

    public SearchHotShopAdapter(List<ShopModel> shops, OnItemClickListener listener) {
        this.shops = shops;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemHotShopBinding binding = SearchItemHotShopBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShopModel item = shops.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return shops.size();
    }

    public void updateItems(List<ShopModel> shops) {
        this.shops.clear();
        this.shops.addAll(shops);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemHotShopBinding binding;

        public ViewHolder(@NonNull SearchItemHotShopBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(shops.get(position));
                }
            });
        }

        public void bind(ShopModel item) {
            binding.searchName.setText(item.getShopName());
            binding.searchRating.setText(String.valueOf(item.getRating()));
            binding.searchRatingIcon.setText(String.valueOf("★"));
        }
    }
}
