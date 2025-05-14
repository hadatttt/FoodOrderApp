package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.databinding.SearchSearchQueryItemBinding;
import com.example.foodorderapp.model.SearchQueryModel;

import java.util.List;

public class SearchSearchQueryAdapter  extends RecyclerView.Adapter<SearchSearchQueryAdapter.SearchQueryViewHolder>{
    private final List<SearchQueryModel> searchQueries;
    private final OnSearchQueryClickListener clickListener;
    private final OnSearchQueryDeleteListener deleteListener;

    public interface OnSearchQueryClickListener {
        void onSearchQueryClick(SearchQueryModel searchQuery);
    }

    public interface OnSearchQueryDeleteListener {
        void onSearchQueryDelete(SearchQueryModel searchQuery);
    }

    public SearchSearchQueryAdapter(List<SearchQueryModel> searchQueries,
                                    OnSearchQueryClickListener clickListener,
                                    OnSearchQueryDeleteListener deleteListener) {
        this.searchQueries = searchQueries;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @Override
    public void onBindViewHolder(@NonNull SearchQueryViewHolder holder, int position) {
        SearchQueryModel searchQuery = searchQueries.get(position);
        holder.bind(searchQuery);
    }

    @NonNull
    @Override
    public SearchQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchSearchQueryItemBinding binding = SearchSearchQueryItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchQueryViewHolder(binding);
    }

    @Override
    public int getItemCount() {
        return searchQueries.size();
    }

    public void updateItems(List<SearchQueryModel> newSearchQueries) {
        if (newSearchQueries == null) {
            return;
        }
        searchQueries.clear();
        searchQueries.addAll(newSearchQueries);
        notifyDataSetChanged();
    }

    class SearchQueryViewHolder extends RecyclerView.ViewHolder {
        private final SearchSearchQueryItemBinding binding;

        public SearchQueryViewHolder(SearchSearchQueryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onSearchQueryClick(searchQueries.get(position));
                }
            });
            binding.chipCloseIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    deleteListener.onSearchQueryDelete(searchQueries.get(position));
                    searchQueries.remove(position);
                    notifyItemRemoved(position);
                }
            });
        }

        public void bind(SearchQueryModel searchQuery) {
            binding.chipText.setText(searchQuery.getKeyword());
        }
    }
}
