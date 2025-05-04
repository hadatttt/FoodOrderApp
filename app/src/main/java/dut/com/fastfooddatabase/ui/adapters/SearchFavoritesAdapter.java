package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.databinding.SearchItemFavouriteBinding;

public class SearchFavoritesAdapter extends RecyclerView.Adapter<SearchFavoritesAdapter.FavoriteViewHolder> {

    private final List<MenuItem> favoriteItems;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(MenuItem item);
    }

    public SearchFavoritesAdapter(List<MenuItem> favoriteItems, OnItemClickListener listener) {
        this.favoriteItems = favoriteItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemFavouriteBinding binding = SearchItemFavouriteBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new FavoriteViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        MenuItem item = favoriteItems.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return favoriteItems.size();
    }

    public void updateItems(List<MenuItem> newFavorites) {
        favoriteItems.clear();
        favoriteItems.addAll(newFavorites);
        notifyDataSetChanged();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemFavouriteBinding binding;

        public FavoriteViewHolder(@NonNull SearchItemFavouriteBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(favoriteItems.get(position));
                }
            });
        }

        public void bind(MenuItem item) {
            binding.favoriteName.setText(item.getName());
            ApplicationRepository.getInstance().getShopRepository().getShopById(item.getShopId(), task -> {
                if (task.isSuccessful()) {
                    Shop shopName =  task.getResult().toObject(Shop.class);
                    binding.shopName.setText(shopName.getName());
                } else {
                    binding.shopName.setText("Unknown shop");
                }
            });
            // Assume shop name is fetched elsewhere or preloaded in MenuItem
            Glide.with(binding.favoriteImage.getContext())
                    .load(item.getImageUrl())
                    .into(binding.favoriteImage);
        }
    }
}