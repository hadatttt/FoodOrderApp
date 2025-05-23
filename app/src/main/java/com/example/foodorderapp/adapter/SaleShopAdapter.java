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
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.ui.DetailShopActivity;

import java.util.Collections;
import java.util.List;

public class SaleShopAdapter extends RecyclerView.Adapter<SaleShopAdapter.ShopViewHolder> {

    private final Context context;
    private List<ShopModel> shopList;

    // Constructor
    public SaleShopAdapter(Context context, List<ShopModel> shopList) {
        this.context = context;
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

    // Phương thức cập nhật danh sách
    public void updateData(List<ShopModel> newShopList) {
        Collections.sort(newShopList, (o1, o2) -> Double.compare(o2.getDiscount(), o1.getDiscount()));
        this.shopList = newShopList;
        notifyDataSetChanged();
    }

    public class ShopViewHolder extends RecyclerView.ViewHolder {
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
            // Load ảnh
            Glide.with(itemView.getContext())
                    .load(shop.getImageUrl())
                    .into(imageShop);

            // Set tên và địa chỉ

            textShopName.setText(shop.getShopName());
            textAddress.setText(shop.getAddress());

            // Hiển thị hoặc ẩn khuyến mãi
            if (shop.getDiscount() > 0) {
                textDiscount.setVisibility(View.VISIBLE);
                textDiscount.setText(String.format("Giảm %.0f%%", shop.getDiscount()));
            } else {
                textDiscount.setVisibility(View.GONE);
            }

            // Xử lý sự kiện khi bấm vào item
            itemView.setOnClickListener(v -> {
                Intent intent = new Intent(context, DetailShopActivity.class);
                intent.putExtra("storeId", shop.getStoreid());
                context.startActivity(intent);
            });
        }
    }
}
