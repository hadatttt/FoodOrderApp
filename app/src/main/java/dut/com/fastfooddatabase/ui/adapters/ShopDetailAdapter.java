package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.databinding.ShopDetailMenuItemBinding;

public class ShopDetailAdapter extends RecyclerView.Adapter<ShopDetailAdapter.ViewHolder> {

    private final List<MenuItem> menuItems;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }

    public ShopDetailAdapter(List<MenuItem> menuItems, OnItemClickListener listener) {
        this.menuItems = menuItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShopDetailMenuItemBinding binding = ShopDetailMenuItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuItem menuItem = menuItems.get(position);
        holder.bind(menuItem);
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public void updateMenuItems(List<MenuItem> newMenuItems) {
        this.menuItems.clear();
        this.menuItems.addAll(newMenuItems);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ShopDetailMenuItemBinding binding;

        public ViewHolder(@NonNull ShopDetailMenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(menuItems.get(position));
                }
            });
        }

        public void bind(MenuItem menuItem) {
            binding.shopDetailItemItemTitle.setText(menuItem.getName());
            binding.shopDetailItemItemPrice.setText(String.valueOf(menuItem.getPrice()));
            binding.shopDetailItemItemDescription.setText(menuItem.getDescription());

            Glide.with(binding.shopDetailItemItemImage.getContext())
                    .load(menuItem.getImageUrl())
                    .into(binding.shopDetailItemItemImage);
        }
    }
}
