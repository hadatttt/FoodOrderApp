package dut.com.fastfooddatabase.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import dut.com.fastfooddatabase.data.models.Order;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.databinding.OrderTrackingItemOrderBinding;

public class OrderTrackingItemAdapter extends RecyclerView.Adapter<OrderTrackingItemAdapter.ViewHolder> {
    private List<String> items;

    public OrderTrackingItemAdapter(List<String> items) {
        this.items = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        OrderTrackingItemOrderBinding binding = OrderTrackingItemOrderBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new OrderTrackingItemAdapter.ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = items.get(position);
        ApplicationRepository.getInstance().getOrderRepository().getOrderById(item, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Order order = task.getResult().toObject(Order.class);
                ApplicationRepository.getInstance().getOrderRepository().getMenuItemsByOrderId(order.getId(), task1 -> {
                    if (task1.isSuccessful() && task1.getResult() != null) {
                    } else {
                        // Handle the error
                    }
                });
            } else {
                // Handle the error
            }
        });

    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private OrderTrackingItemOrderBinding binding;
        public ViewHolder(@NonNull OrderTrackingItemOrderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
