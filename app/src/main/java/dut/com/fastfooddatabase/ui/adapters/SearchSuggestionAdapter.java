package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.databinding.SearchItemSuggestionBinding;

public class SearchSuggestionAdapter extends RecyclerView.Adapter<SearchSuggestionAdapter.SuggestionViewHolder> {

    private final List<Shop> suggestions;
    private final OnSuggestionClickListener listener;

    public interface OnSuggestionClickListener {
        void onSuggestionClick(Shop suggestion);
    }

    public SearchSuggestionAdapter(List<Shop> suggestions, OnSuggestionClickListener listener) {
        this.suggestions = suggestions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SuggestionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemSuggestionBinding binding = SearchItemSuggestionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new SuggestionViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestionViewHolder holder, int position) {
        Shop suggestion = suggestions.get(position);
        holder.bind(suggestion);
    }

    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    public void updateItems(List<Shop> newSuggestions) {
        suggestions.clear();
        suggestions.addAll(newSuggestions);
        notifyDataSetChanged();
    }

    class SuggestionViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemSuggestionBinding binding;

        public SuggestionViewHolder(@NonNull SearchItemSuggestionBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onSuggestionClick(suggestions.get(position));
                }
            });
        }

        public void bind(Shop suggestion) {
            binding.searchName.setText(suggestion.getName());
            binding.searchRating.setText(String.valueOf(suggestion.getRating()));
            binding.searchRatingIcon.setText(String.valueOf("★"));
        }
    }
}