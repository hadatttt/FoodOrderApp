package com.example.foodorderapp.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;

import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    private final List<String> historyList;
    private final OnItemClickListener listener;
    private final OnCloseClickListener closeListener;

    // Interface for handling chip click
    public interface OnItemClickListener {
        void onItemClick(String historyItem);
    }

    // Interface for handling close icon click
    public interface OnCloseClickListener {
        void onCloseClick(String historyItem);
    }

    // Constructor
    public HistoryAdapter(List<String> historyList, OnItemClickListener listener, OnCloseClickListener closeListener) {
        this.historyList = historyList;
        this.listener = listener;
        this.closeListener = closeListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_history_chip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String historyItem = historyList.get(position);
        holder.bind(historyItem, listener, closeListener);
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    // ViewHolder class
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView chipText;
        private final ImageView closeIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            chipText = itemView.findViewById(R.id.chip_text);
            closeIcon = itemView.findViewById(R.id.chip_close_icon);
        }

        public void bind(final String historyItem, final OnItemClickListener listener, final OnCloseClickListener closeListener) {
            chipText.setText(historyItem);
            itemView.setOnClickListener(v -> listener.onItemClick(historyItem));
            closeIcon.setOnClickListener(v -> closeListener.onCloseClick(historyItem));
        }
    }

    // Method to update history list
    public void updateList(List<String> newList) {
        historyList.clear();
        historyList.addAll(newList);
        notifyDataSetChanged();
    }
}