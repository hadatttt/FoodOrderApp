package com.example.foodorderapp.adapter;

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

import java.util.Collections;
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
        holder.bind(shop);  // Sử dụng đối tượng 'shop' ở đây
    }

    @Override
    public int getItemCount() {
        return shopList.size();
    }

    // Phương thức để cập nhật dữ liệu trong adapter
    public void updateData(List<ShopModel> newShopList) {
        // Sắp xếp giảm dần theo discount
        Collections.sort(newShopList, (o1, o2) -> Double.compare(o2.getDiscount(), o1.getDiscount()));
        this.shopList = newShopList;
        notifyDataSetChanged();
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
            // Load ảnh
            Glide.with(itemView.getContext())
                    .load(shop.getImageUrl())
                    .into(imageShop);

            // Set tên và địa chỉ
            textShopName.setText(shop.getShopName());
            textAddress.setText(shop.getAddress());

            // Hiển thị hoặc ẩn giảm giá
            if (shop.getDiscount() > 0) {
                textDiscount.setVisibility(View.VISIBLE);
                textDiscount.setText(String.format("Giảm %.0f%%", shop.getDiscount()));
            } else {
                textDiscount.setVisibility(View.GONE); // Ẩn khung nếu không có giảm giá
            }
        }

    }
}
