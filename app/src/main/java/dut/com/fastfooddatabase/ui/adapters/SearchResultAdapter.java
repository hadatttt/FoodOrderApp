package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import dut.com.fastfooddatabase.data.models.SearchResultItem;
import dut.com.fastfooddatabase.databinding.SearchItemSuggestionBinding;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> {

    private final List<SearchResultItem> searchResults = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(SearchResultItem item);
    }

    public SearchResultAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemSuggestionBinding binding = SearchItemSuggestionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SearchResultViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchResultViewHolder holder, int position) {
        SearchResultItem item = searchResults.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void updateResults(List<SearchResultItem> results) {
        searchResults.clear();
        searchResults.addAll(results);
        notifyDataSetChanged();
    }

    class SearchResultViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemSuggestionBinding binding;

        public SearchResultViewHolder(@NonNull SearchItemSuggestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(searchResults.get(position));
                }
            });
        }

        public void bind(SearchResultItem item) {
            binding.searchName.setText(item.getName());
            binding.searchRating.setText(String.format("%.1f", item.getRating()));
            binding.searchRatingIcon.setVisibility(View.VISIBLE); // Always show rating icon for consistency

            Glide.with(binding.searchImage.getContext())
                    .load(item.getImageUrl())
                    .into(binding.searchImage);
        }
    }
}