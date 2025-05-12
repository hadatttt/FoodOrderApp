package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.ShopModel;

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

    // Phương thức để cập nhật dữ liệu trong adapter
    public void updateData(List<ShopModel> newShopList) {
        this.shopList = newShopList;
        notifyDataSetChanged();  // Thông báo RecyclerView cập nhật dữ liệu
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
            // Chuyển từ tên tài nguyên drawable thành ID thực tế

            int resId = itemView.getContext().getResources().getIdentifier(
                    shop.getImageUrl(), "drawable", itemView.getContext().getPackageName());
            imageShop.setImageResource(resId);
            textShopName.setText(shop.getShopName());
            textAddress.setText(shop.getAddress());
            // Hiển thị giảm giá
            textDiscount.setText(String.format("Giảm %.0f%%", shop.getDiscount()));
        }
    }
}
