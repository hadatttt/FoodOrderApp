package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.SearchResult;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {

    private final List<SearchResult> searchResults;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchResult searchResult);
    }

    public SearchResultsAdapter(List<SearchResult> searchResults, OnItemClickListener listener) {
        this.searchResults = searchResults;
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
        SearchResult searchResult = searchResults.get(position);
        holder.bind(searchResult, listener);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameTextView;
        private final TextView ratingTextView;
        private final TextView ratingIconTextView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.restaurant_image);
            nameTextView = itemView.findViewById(R.id.restaurant_name);
            ratingTextView = itemView.findViewById(R.id.restaurant_rating);
            ratingIconTextView = itemView.findViewById(R.id.restaurant_rating_icon);
        }

        public void bind(final SearchResult searchResult, final OnItemClickListener listener) {
            imageView.setImageResource(searchResult.getImageRes());
            nameTextView.setText(searchResult.getName());
            if (searchResult.getRating() >= 0) {
                ratingTextView.setText(String.valueOf(searchResult.getRating()));
                ratingTextView.setVisibility(View.VISIBLE);
                ratingIconTextView.setVisibility(View.VISIBLE);
            } else {
                ratingTextView.setVisibility(View.GONE);
                ratingIconTextView.setVisibility(View.GONE);
            }
            itemView.setOnClickListener(v -> listener.onItemClick(searchResult));
        }
    }

    public void updateList(List<SearchResult> newList) {
        searchResults.clear();
        searchResults.addAll(newList);
        notifyDataSetChanged();
    }
}