package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dut.com.fastfooddatabase.R;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.databinding.HomeShopItemBinding;

public class HomeShopItemAdapter extends RecyclerView.Adapter<HomeShopItemAdapter.ShopViewHolder> {

    private List<Shop> shops;

    public HomeShopItemAdapter() {
        this.shops = null;
    }

    @NonNull
    @Override
    public ShopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_shop_item, parent, false);
        return new ShopViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopViewHolder holder, int position) {
        Shop shop = shops.get(position);
        holder.bind(shop);
    }

    @Override
    public int getItemCount() {
        return (shops == null) ? 0 : shops.size();
    }

    public void setShops(List<Shop> shops) {
        this.shops = shops;
        notifyDataSetChanged();
    }

    public static class ShopViewHolder extends RecyclerView.ViewHolder {

        private final HomeShopItemBinding binding;

        public ShopViewHolder(View itemView) {
            super(itemView);
            binding = HomeShopItemBinding.bind(itemView);
        }

        public void bind(Shop shop) {
            binding.homeShopItemName.setText(shop.getName());
            binding.homeShopItemRating.setText(String.valueOf(shop.getRating()));
            Glide.with(binding.homeShopItemImage.getContext())
                    .load(shop.getImageUrl())
                    .into(binding.homeShopItemImage);
        }
    }
}
