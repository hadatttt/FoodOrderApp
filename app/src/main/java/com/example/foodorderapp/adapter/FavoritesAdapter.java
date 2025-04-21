package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.FavoriteItem;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.ViewHolder> {

    private final List<FavoriteItem> favoriteList;
    private final OnItemClickListener listener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(FavoriteItem favoriteItem);
    }

    // Constructor
    public FavoritesAdapter(List<FavoriteItem> favoriteList, OnItemClickListener listener) {
        this.favoriteList = favoriteList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteItem favoriteItem = favoriteList.get(position);
        holder.bind(favoriteItem, listener);
    }

    @Override
    public int getItemCount() {
        return favoriteList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView favoriteImage;
        private final TextView favoriteName;
        private final TextView restaurantName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            favoriteImage = itemView.findViewById(R.id.favorite_image);
            favoriteName = itemView.findViewById(R.id.favorite_name);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
        }

        public void bind(final FavoriteItem favoriteItem, final OnItemClickListener listener) {
            favoriteImage.setImageResource(favoriteItem.getImageRes());
            favoriteName.setText(favoriteItem.getName());
            restaurantName.setText(favoriteItem.getRestaurant());
            itemView.setOnClickListener(v -> listener.onItemClick(favoriteItem));
        }
    }
}

