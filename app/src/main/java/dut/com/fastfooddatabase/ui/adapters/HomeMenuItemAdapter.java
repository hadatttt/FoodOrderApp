package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.databinding.HomeMenuItemBinding;

public class HomeMenuItemAdapter extends ListAdapter<MenuItem, HomeMenuItemAdapter.MenuItemViewHolder> {

    private OnItemClickListener listener;

    public HomeMenuItemAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<MenuItem> DIFF_CALLBACK = new DiffUtil.ItemCallback<MenuItem>() {
        @Override
        public boolean areItemsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.getId().equals(newItem.getId());
        }

        @Override
        public boolean areContentsTheSame(@NonNull MenuItem oldItem, @NonNull MenuItem newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getPrice() == newItem.getPrice() &&
                    oldItem.getRating() == newItem.getRating() &&
                    oldItem.getImageUrl().equals(newItem.getImageUrl());
        }
    };

    @NonNull
    @Override
    public MenuItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HomeMenuItemBinding binding = HomeMenuItemBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MenuItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuItemViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    public void setMenuItem(@NonNull List<MenuItem> menuItems) {
        submitList(menuItems);
    }

    public interface OnItemClickListener {
        void onItemClick(MenuItem menuItem);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class MenuItemViewHolder extends RecyclerView.ViewHolder {
        private final HomeMenuItemBinding binding;

        public MenuItemViewHolder(HomeMenuItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

            binding.getRoot().setOnClickListener(v -> {
//                int position = getBindingAdapterPosition();
//                if (position != RecyclerView.NO_POSITION && listener != null) {
//                    listener.onItemClick(getItem(position));
//                }
            });
        }

        public void bind(MenuItem item) {
            binding.homeMenuItemName.setText(item.getName());
            binding.homeMenuItemPrice.setText(String.format("$%,d", item.getPrice()));
            binding.homeMenuItemRating.setText(String.format("%.1f", item.getRating()));

            Glide.with(binding.homeMenuItemImage.getContext())
                    .load(item.getImageUrl())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(new RequestOptions()
                            .centerCrop())
                    .into(binding.homeMenuItemImage);
        }
    }
}