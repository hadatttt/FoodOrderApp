package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.Restaurant;

import java.util.List;

public class SuggestionsAdapter extends RecyclerView.Adapter<SuggestionsAdapter.ViewHolder> {

    private final List<Restaurant> restaurantList;
    private final OnItemClickListener listener;

    // Interface for handling item clicks
    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    // Constructor
    public SuggestionsAdapter(List<Restaurant> restaurantList, OnItemClickListener listener) {
        this.restaurantList = restaurantList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_suggestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurant restaurant = restaurantList.get(position);
        holder.bind(restaurant, listener);
    }

    @Override
    public int getItemCount() {
        return restaurantList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView restaurantImage;
        private final TextView restaurantName;
        private final TextView restaurantRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            restaurantImage = itemView.findViewById(R.id.restaurant_image);
            restaurantName = itemView.findViewById(R.id.restaurant_name);
            restaurantRating = itemView.findViewById(R.id.restaurant_rating);
        }

        public void bind(final Restaurant restaurant, final OnItemClickListener listener) {
            restaurantImage.setImageResource(restaurant.getImageRes());
            restaurantName.setText(restaurant.getName());
            restaurantRating.setText(String.valueOf(restaurant.getRating()));
            itemView.setOnClickListener(v -> listener.onItemClick(restaurant));
        }
    }
}


