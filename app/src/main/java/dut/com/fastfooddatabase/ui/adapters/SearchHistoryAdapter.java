package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dut.com.fastfooddatabase.data.models.SearchHistory;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.databinding.SearchItemHistoryBinding;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder> {

    private final List<SearchHistory> historyItems;
    private final OnHistoryItemClickListener clickListener;
    private final OnHistoryItemDeleteListener deleteListener;

    public interface OnHistoryItemClickListener {
        void onHistoryItemClick(SearchHistory historyItem);
    }

    public interface OnHistoryItemDeleteListener {
        void onHistoryItemDelete(SearchHistory historyItem);
    }

    public SearchHistoryAdapter(List<SearchHistory> historyItems,
                                OnHistoryItemClickListener clickListener,
                                OnHistoryItemDeleteListener deleteListener) {
        this.historyItems = historyItems;
        this.clickListener = clickListener;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SearchItemHistoryBinding binding = SearchItemHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new HistoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryViewHolder holder, int position) {
        SearchHistory historyItem = historyItems.get(position);
        holder.bind(historyItem);
    }

    @Override
    public int getItemCount() {
        return historyItems.size();
    }

    public void updateItems(List<SearchHistory> newHistoryItems) {
        if (newHistoryItems == null) {
            ApplicationRepository.getInstance().getSearchHistoryRepository().deleteAllSearchHistoryByUserId("0", task -> {
            });
            return;
        }
        this.historyItems.clear();
        this.historyItems.addAll(newHistoryItems);
        notifyDataSetChanged();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {
        private final SearchItemHistoryBinding binding;

        public HistoryViewHolder(@NonNull SearchItemHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    clickListener.onHistoryItemClick(historyItems.get(position));
                }
            });

            binding.chipCloseIcon.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && deleteListener != null && position < historyItems.size()) {
                    deleteListener.onHistoryItemDelete(historyItems.get(position));
                }
            });
        }

        public void bind(SearchHistory historyItem) {
            binding.chipText.setText(historyItem.getKeyword());
        }
    }
}