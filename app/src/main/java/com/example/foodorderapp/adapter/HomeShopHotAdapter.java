package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.HomeHotShopItemBinding;
import com.example.foodorderapp.model.ShopModel;

import java.util.ArrayList;
import java.util.List;

public class HomeShopHotAdapter extends RecyclerView.Adapter<HomeShopHotAdapter.ShopViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(ShopModel shop);
    }

    protected static class ShopViewHolder extends RecyclerView.ViewHolder {

        private final HomeHotShopItemBinding binding;

        public ShopViewHolder(View itemView, OnItemClickListener listener) {
            super(itemView);
            binding = HomeHotShopItemBinding.bind(itemView);
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    ShopModel shop = (ShopModel) itemView.getTag();
                    listener.onItemClick(shop);
                }
            });

        }

        public void bind(ShopModel shop) {
            binding.homeShopItemName.setText(shop.getShopName());
            binding.homeShopItemRating.setText(String.valueOf(shop.getRating()));
            Glide.with(binding.homeShopItemImage.getContext())
                    .load(shop.getImageUrl())
                    .into(binding.homeShopItemImage);
            binding.getRoot().setTag(shop);
        }
    }

    private List<ShopModel> shops = new ArrayList<>();
    private final OnItemClickListener onItemClickListener;

    public HomeShopHotAdapter(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_hot_shop_item, parent, false);
        return new ShopViewHolder(view, onItemClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopModel shop = shops.get(position);
        holder.bind(shop);
    }

    public int getItemCount() {
        return (shops == null) ? 0 : shops.size();
    }

    public void setShops(List<ShopModel> shops) {
        this.shops.clear();
        this.shops.addAll(shops);
        notifyDataSetChanged();
    }
}