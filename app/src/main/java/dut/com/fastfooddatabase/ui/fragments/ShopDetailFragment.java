package dut.com.fastfooddatabase.ui.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import dut.com.fastfooddatabase.R;
import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.databinding.FragmentShopDetailBinding;
import dut.com.fastfooddatabase.ui.adapters.ShopDetailAdapter;
import dut.com.fastfooddatabase.ui.viewmodels.ShopDetailViewModel;

public class ShopDetailFragment extends Fragment {

    private String shopId = "0";

    private FragmentShopDetailBinding binding;
    private ShopDetailViewModel viewModel;
    private ShopDetailAdapter adapter;

    public ShopDetailFragment(String shopId) {
        this.shopId = shopId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentShopDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ShopDetailViewModel.class);
        viewModel.setShopId(shopId);

        ApplicationRepository.getInstance().getShopRepository().getShopById(shopId, task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                Shop shop = task.getResult().toObject(Shop.class);
                binding.shopDetailName.setText(shop.getName());
                binding.shopDetailRating.setText(String.valueOf(shop.getRating()));
                binding.shopDetailDescription.setText(shop.getDescription());
                Glide.with(binding.shopDetailImage.getContext())
                        .load(shop.getImageUrl())
                        .into(binding.shopDetailImage);
            }
        });
        setUpRecyclerView();
        loadData();
    }

    private void setUpRecyclerView() {
        binding.shopDetailMenuItemsRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        adapter = new ShopDetailAdapter(new ArrayList<>(), this::onMenuItemClick);
        binding.shopDetailMenuItemsRecyclerView.setAdapter(adapter);
    }

    private void onMenuItemClick(MenuItem menuItem) {
        // Handle menu item click
        // For example, navigate to a detailed view of the menu item
    }

    private void loadData() {
        viewModel.getMenuItems().observe(getViewLifecycleOwner(), menuItems -> {
            adapter.updateMenuItems(menuItems);
        });
    }
}