package dut.com.fastfooddatabase.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import java.util.List;
import dut.com.fastfooddatabase.databinding.FragmentHomeBinding;
import dut.com.fastfooddatabase.ui.adapters.HomeMenuItemAdapter;
import dut.com.fastfooddatabase.ui.adapters.HomeShopItemAdapter;
import dut.com.fastfooddatabase.ui.viewmodels.HomeViewModel;

public class HomeFragment extends Fragment {
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeMenuItemAdapter menuItemAdapter;
    private HomeShopItemAdapter shopItemAdapter;
    private List<Button> categoryButtons;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupRecyclerViews();
        setupCategoryButtons();
        setupClickListeners();
        observeViewModel();

        viewModel.loadInitialData();
    }

    private void setupRecyclerViews() {
        // Hot food menu items
        menuItemAdapter = new HomeMenuItemAdapter();
        binding.recyclerHotFood.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerHotFood.setAdapter(menuItemAdapter);

        // Shop items
        shopItemAdapter = new HomeShopItemAdapter();
        binding.recyclerSaleShop.setLayoutManager(
                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.recyclerSaleShop.setAdapter(shopItemAdapter);
    }

    private void setupCategoryButtons() {
        categoryButtons = List.of(
                binding.btnAll,
                binding.btnBurger,
                binding.btnChicken,
                binding.btnPizza,
                binding.btnSpaghetti,
                binding.btnPotato
        );
    }

    private void setupClickListeners() {
        binding.btnAll.setOnClickListener(v -> selectCategory("All", binding.btnAll));
        binding.btnBurger.setOnClickListener(v -> selectCategory("Burger", binding.btnBurger));
        binding.btnChicken.setOnClickListener(v -> selectCategory("Chicken", binding.btnChicken));
        binding.btnPizza.setOnClickListener(v -> selectCategory("Pizza", binding.btnPizza));
        binding.btnSpaghetti.setOnClickListener(v -> selectCategory("Spaghetti", binding.btnSpaghetti));
        binding.btnPotato.setOnClickListener(v -> selectCategory("Potato", binding.btnPotato));

        menuItemAdapter.setOnItemClickListener(menuItem -> {
            // Navigate to menu item detail
        });

//        shopItemAdapter.setOnItemClickListener(shop -> {
//            // Navigate to shop detail
//        });
    }

    private void selectCategory(String category, Button selectedButton) {
        for (Button button : categoryButtons) {
            button.setBackgroundTintList(android.content.res.ColorStateList.valueOf(
                    button == selectedButton ? 0xFFFFD700 : 0xFFEEEEEE));
        }
        viewModel.loadMenuItemsByCategory(category);
    }

    private void observeViewModel() {
        viewModel.getMenuItems().observe(getViewLifecycleOwner(), menuItems -> {
            menuItemAdapter.submitList(menuItems);  // Replace setMenuItem with submitList
        });

        viewModel.getShops().observe(getViewLifecycleOwner(), shops -> {
            shopItemAdapter.setShops(shops);
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null && !message.isEmpty()) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}