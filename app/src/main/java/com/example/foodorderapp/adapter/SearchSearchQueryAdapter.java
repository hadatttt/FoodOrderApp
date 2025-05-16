package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.databinding.SearchSearchQueryItemBinding;
import com.example.foodorderapp.model.SearchQueryModel;
import com.example.foodorderapp.service.SearchQueryService;

import java.util.List;

public class SearchSearchQueryAdapter extends RecyclerView.Adapter<SearchSearchQueryAdapter.SearchQueryViewHolder> {
    private final List<SearchQueryModel> searchQueries;
    private final OnSearchQueryClickListener clickListener;
    private final SearchQueryService searchQueryService;
    private final String currentUserId;

    public interface OnSearchQueryClickListener {
        void onSearchQueryClick(SearchQueryModel searchQuery);
    }

    public SearchSearchQueryAdapter(List<SearchQueryModel> searchQueries,
                                    OnSearchQueryClickListener clickListener,
                                    SearchQueryService searchQueryService,
                                    String currentUserId) {
        this.searchQueries = searchQueries;
        this.clickListener = clickListener;
        this.searchQueryService = searchQueryService;
        this.currentUserId = currentUserId;
    }

    @NonNull
    @Override
    public SearchQueryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchSearchQueryItemBinding binding = SearchSearchQueryItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchQueryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchQueryViewHolder holder, int position) {
        holder.bind(searchQueries.get(position));
    }

    @Override
    public int getItemCount() {
        return searchQueries.size();
    }

    public void updateItems(List<SearchQueryModel> newSearchQueries) {
        if (newSearchQueries == null) return;
        searchQueries.clear();
        searchQueries.addAll(newSearchQueries);
        notifyDataSetChanged();
    }

    class SearchQueryViewHolder extends RecyclerView.ViewHolder {
        private final SearchSearchQueryItemBinding binding;

        public SearchQueryViewHolder(SearchSearchQueryItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            // Click item callback
            binding.getRoot().setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos != RecyclerView.NO_POSITION) {
                    clickListener.onSearchQueryClick(searchQueries.get(pos));
                }
            });

            // Xóa item và gọi service
            binding.chipCloseIcon.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;

                SearchQueryModel itemToDelete = searchQueries.get(pos);

                searchQueryService.deleteSearchQueryByKeywordAndUserId(currentUserId, itemToDelete.getKeyword())
                        .addOnSuccessListener(aVoid -> {
                            searchQueries.remove(pos);
                            notifyItemRemoved(pos);
                        })
                        .addOnFailureListener(e -> {
                            // Có thể show Toast hoặc log lỗi
                        });
            });
        }

        public void bind(SearchQueryModel searchQuery) {
            binding.chipText.setText(searchQuery.getKeyword());
        }
    }
}
