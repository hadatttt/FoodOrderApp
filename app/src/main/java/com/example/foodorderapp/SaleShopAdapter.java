package com.example.foodorderapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SaleShopAdapter extends RecyclerView.Adapter<SaleShopAdapter.ShopViewHolder> {

    private List<ShopModel> shopList;

    // Constructor
    public SaleShopAdapter(List<ShopModel> shopList) {
        this.shopList = shopList;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shop_sale, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        ShopModel shop = shopList.get(position);
        holder.bind(shop);
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageShop;
        private final TextView textShopName;
        private final TextView textAddress;
        private final TextView textDiscount;

        public ShopViewHolder(@NonNull View itemView) {
            super(itemView);
            imageShop = itemView.findViewById(R.id.imageShop);
            textShopName = itemView.findViewById(R.id.textShopName);
            textAddress = itemView.findViewById(R.id.address);
            textDiscount = itemView.findViewById(R.id.Discount);
        }

        public void bind(ShopModel shop) {
            imageShop.setImageResource(shop.getImageResource());
            textShopName.setText(shop.getShopName());
            textAddress.setText(shop.getAddress());
            // Format float discount as "Giảm X%"
            textDiscount.setText(String.format("Giảm %.0f%%", shop.getDiscount()));
        }
    }
}