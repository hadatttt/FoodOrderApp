package com.example.foodorderapp.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.foodorderapp.adapter.HomeShopHotAdapter;
import com.example.foodorderapp.adapter.HomeShopNewAdapter;
import com.example.foodorderapp.adapter.HotFoodAdapter;
import com.example.foodorderapp.adapter.SaleShopAdapter;
import com.example.foodorderapp.databinding.FragmentHomeBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.AllHotFoodActivity;
import com.example.foodorderapp.ui.AllShopSaleActivity;
import com.example.foodorderapp.ui.CartActivity;
import com.example.foodorderapp.ui.DetailShopActivity;
import com.example.foodorderapp.ui.LoginActivity;
import com.example.foodorderapp.ui.RegisterActivity1;
import com.example.foodorderapp.ui.SearchActivity;
import com.example.foodorderapp.viewmodel.HomeViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private UserService userService = new UserService();
    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private HomeShopHotAdapter hotShopAdapter;
    private HomeShopNewAdapter newShopAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViewModel();
        setupRecyclerViews();
        observeData();

        setUpComponents();
        viewModel.loadInitialData();

        setupSearchClick();

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);
    }

    private void setUpComponents() {
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // User authenticated but no profile document exists
                        Intent intent = new Intent(getActivity(), RegisterActivity1.class);
                        startActivity(intent);
                    } else {
                        // Profile exists, check if it's complete
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        if (userModel == null || userModel.getFullName() == null || userModel.getFullName().isEmpty()) {
                            // Profile exists but is incomplete
                            Intent intent = new Intent(getActivity(), RegisterActivity1.class);
                            startActivity(intent);
                        } else {
                            binding.mainUserAddress.setText(userModel.getAddress());
                            binding.cartBadgeContainer.setOnClickListener(v -> {
                                Intent intent = new Intent(getActivity(), CartActivity.class);
                                startActivity(intent);
                            });
                            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            new CartService().getCartByUserId(userId)
                                    .addOnSuccessListener(querySnapshot -> {
                                        int cartItemCount = querySnapshot.size();
                                        binding.textCartCount.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
                                        if (cartItemCount > 0) {
                                            binding.textCartCount.setText(String.valueOf(cartItemCount));
                                        }
                                    })
                                    .addOnFailureListener(e -> Log.e(TAG, "Error loading cart items", e));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivity(intent);
                });
    }

    private void setupRecyclerViews() {
        hotShopAdapter = new HomeShopHotAdapter(this::navigateToShopDetail);
        newShopAdapter = new HomeShopNewAdapter(this::navigateToShopDetail);

        binding.mainRecyclerViewHotShop.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.mainRecyclerViewHotShop.setAdapter(hotShopAdapter);

        LinearLayoutManager horizontalLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        binding.mainRecyclerViewNewShop.setLayoutManager(horizontalLayout);
        binding.mainRecyclerViewNewShop.setAdapter(newShopAdapter);
    }

    private void observeData() {
        viewModel.getHotShops().observe(getViewLifecycleOwner(), shops -> {
            if (shops != null) {
                hotShopAdapter.setShops(shops);
            }
        });

        viewModel.getNewShops().observe(getViewLifecycleOwner(), shops -> {
            if (shops != null) {
                newShopAdapter.setShops(shops);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            binding.mainLoadingOverlay.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });
    }

    private void navigateToShopDetail(ShopModel shop) {
        if (getContext() != null) {
            Intent intent = new Intent(getContext(), DetailShopActivity.class);
            intent.putExtra("storeId", shop.getStoreid());
            intent.putExtra("foodId", -1);
            startActivity(intent);
        } else {
            Log.e(TAG, "Context is null, cannot navigate to shop detail.");
        }
    }

    private void setupSearchClick() {
        binding.mainSearchLayout.setOnClickListener(v -> {
            Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
            startActivity(searchIntent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
