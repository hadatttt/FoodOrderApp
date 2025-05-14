package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.SavedStateViewModelFactory;
import androidx.lifecycle.ViewModelProvider;

import com.example.foodorderapp.adapter.SearchHotFoodAdapter;
import com.example.foodorderapp.adapter.SearchHotShopAdapter;
import com.example.foodorderapp.adapter.SearchResultAdapter;
import com.example.foodorderapp.adapter.SearchSearchQueryAdapter;
import com.example.foodorderapp.databinding.ActivitySearchBinding;
import com.example.foodorderapp.model.SearchQueryModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.viewmodel.SearchViewModel;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private static final long DEBOUNCE_DELAY_MS = 300;

    private UserService userService;
    private SearchViewModel searchViewModel;
    private SearchSearchQueryAdapter searchQueryAdapter;
    private SearchResultAdapter searchResultAdapter;
    private SearchHotShopAdapter searchHotShopAdapter;
    private SearchHotFoodAdapter searchHotFoodAdapter;
    private ActivitySearchBinding binding;
    private Handler searchHandler;
    private Runnable searchRunnable;
    private boolean isLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userService = new UserService();
        searchHandler = new Handler(Looper.getMainLooper());

        setupSearchInput();
        setupBackButton();
        authenticateUser();
    }

    private void setupSearchInput() {
        binding.searchInput.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN)) {
                performSearch(binding.searchInput.getText().toString().trim());
                return true;
            }
            return false;
        });

        binding.searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }
                searchRunnable = () -> {
                    showLoading(true);
                    performSearch(s.toString().trim());
                };
                searchHandler.postDelayed(searchRunnable, DEBOUNCE_DELAY_MS);
            }
        });

        binding.searchClearHistoryBtn.setOnClickListener(v -> searchViewModel.clearSearchQuery());
    }

    private void setupBackButton() {
        binding.searchBtnBack.setOnClickListener(v -> onBackPressed());
    }

    private void authenticateUser() {
        showLoading(true);
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {
                            initializeViewModelAndAdapters();
                        } else {
                            handleAuthError("Invalid user data");
                        }
                    } else {
                        handleAuthError("User document does not exist");
                    }
                })
                .addOnFailureListener(e -> handleAuthError("Failed to fetch user data: " + e.getMessage()));
    }

    private void initializeViewModelAndAdapters() {
        searchViewModel = new ViewModelProvider(this,
                new SavedStateViewModelFactory(getApplication(), this))
                .get(SearchViewModel.class);

        searchQueryAdapter = new SearchSearchQueryAdapter(new ArrayList<>(),
                this::performSearchFromHistory,
                searchViewModel::removeFromSearchHistory);

        searchResultAdapter = new SearchResultAdapter(item -> {
            Log.d(TAG, "Search result clicked: shopId=" + item.getShopId() + ", foodId=" + item.getFoodId());
            Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
            navigateToDetail(item.getShopId(), item.getFoodId());
        });

        searchHotShopAdapter = new SearchHotShopAdapter(new ArrayList<>(),
                shop -> {
                    Log.d(TAG, "Hot shop clicked: storeId=" + shop.getStoreid());
                    Toast.makeText(this, "Clicked shop: " + shop.getShopName(), Toast.LENGTH_SHORT).show();
                    navigateToDetail(shop.getStoreid(), -1);
                });

        searchHotFoodAdapter = new SearchHotFoodAdapter(new ArrayList<>(),
                food -> {
                    Log.d(TAG, "Suggested food clicked: storeId=" + food.getStoreId() + ", foodId=" + food.getFoodId());
                    Toast.makeText(this, "Clicked food: " + food.getName(), Toast.LENGTH_SHORT).show();
                    navigateToDetail(food.getStoreId(), food.getFoodId());
                });

        binding.searchHistoryRecyclerView.setAdapter(searchQueryAdapter);
        binding.searchResultsRecyclerView.setAdapter(searchResultAdapter);
        binding.searchSuggestionsRecyclerView.setAdapter(searchHotShopAdapter);
        binding.searchFavoritesRecyclerView.setAdapter(searchHotFoodAdapter);

        observeViewModel();
        showLoading(false);
    }

    private void observeViewModel() {
        searchViewModel.getSearchQueries().observe(this, queries -> {
            if (queries != null) {
                searchQueryAdapter.updateItems(queries);
                updateEmptyHistoryVisibility(queries);
            } else {
                searchQueryAdapter.updateItems(new ArrayList<>());
                updateEmptyHistoryVisibility(new ArrayList<>());
            }
        });

        searchViewModel.getSearchResults().observe(this, results -> {
            if (results != null) {
                searchResultAdapter.updateResults(results);
                updateSearchResultsVisibility(!results.isEmpty());
            } else {
                searchResultAdapter.updateResults(new ArrayList<>());
                updateSearchResultsVisibility(false);
            }
            showLoading(false); // Ẩn loading sau khi có kết quả
        });

        searchViewModel.getHotShops().observe(this, shops -> {
            if (shops != null) {
                searchHotShopAdapter.updateItems(shops);
            } else {
                searchHotShopAdapter.updateItems(new ArrayList<>());
            }
        });

        searchViewModel.getSuggestions().observe(this, foods -> {
            if (foods != null) {
                searchHotFoodAdapter.updateItems(foods);
            } else {
                searchHotFoodAdapter.updateItems(new ArrayList<>());
            }
        });
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            showDefaultUI();
            binding.searchInput.requestFocus();
            return;
        }

        SearchQueryModel newQuery = new SearchQueryModel(
                userService.getUserId(),
                keyword,
                new Date()
        );
        searchViewModel.addToSearchHistory(newQuery);

        searchViewModel.performSearch(keyword);
        binding.searchInput.requestFocus();
        binding.searchInput.setSelection(keyword.length());
    }

    private void performSearchFromHistory(SearchQueryModel query) {
        if (query != null && query.getKeyword() != null) {
            binding.searchInput.setText(query.getKeyword());
            performSearch(query.getKeyword());
        }
    }

    private void navigateToDetail(int storeId, int foodId) {
        try {
            Intent intent;
            if (storeId != -1 && foodId == -1) {
                intent = new Intent(this, DetailShopActivity.class);
                intent.putExtra("storeId", storeId);
            } else if (storeId != -1 && foodId != -1) {
                intent = new Intent(this, DetailFoodActivity.class);
                intent.putExtra("storeId", storeId);
                intent.putExtra("foodId", foodId);
            } else {
                Log.w(TAG, "Invalid navigation: storeId=" + storeId + ", foodId=" + foodId);
                Toast.makeText(this, "Cannot navigate: Invalid item", Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, "Navigation failed: " + e.getMessage(), e);
            Toast.makeText(this, "Error navigating", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateSearchResultsVisibility(boolean hasResults) {
        binding.searchResultsRecyclerView.setVisibility(hasResults ? View.VISIBLE : View.GONE);
        binding.noResultsTextView.setVisibility(hasResults ? View.GONE : View.VISIBLE);

        String query = binding.searchInput.getText().toString().trim();
        if (query.isEmpty()) {
            showDefaultUI();
        } else {
            showSearchResultsUI();
        }
    }

    private void showDefaultUI() {
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.noResultsTextView.setVisibility(View.GONE);
        binding.txtEmptyHistory.setVisibility(View.GONE);
        binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
        binding.searchClearHistoryBtn.setVisibility(View.VISIBLE);
        binding.searchSuggestionsRecyclerView.setVisibility(View.VISIBLE);
        binding.searchSuggestionsTitle.setVisibility(View.VISIBLE);
        binding.searchFavoritesTitle.setVisibility(View.VISIBLE);
        binding.searchFavoritesRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showSearchResultsUI() {
        binding.searchHistoryTitle.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);
        binding.noResultsTextView.setVisibility(View.GONE);
        binding.searchHistoryRecyclerView.setVisibility(View.GONE);
        binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
        binding.searchClearHistoryBtn.setVisibility(View.GONE);
        binding.txtEmptyHistory.setVisibility(View.GONE);
        binding.searchSuggestionsTitle.setVisibility(View.GONE);
        binding.searchSuggestionsRecyclerView.setVisibility(View.GONE);
        binding.searchFavoritesTitle.setVisibility(View.GONE);
        binding.searchFavoritesRecyclerView.setVisibility(View.GONE);
    }

    private void updateEmptyHistoryVisibility(List<SearchQueryModel> historyItems) {
        binding.txtEmptyHistory.setVisibility(
                historyItems == null || historyItems.isEmpty() ? View.VISIBLE : View.GONE
        );
    }

    private void showLoading(boolean show) {
        if (isLoading == show) return;
        isLoading = show;
        binding.progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void handleAuthError(String message) {
        Log.e(TAG, message);
        Toast.makeText(this, "Authentication error: " + message, Toast.LENGTH_LONG).show();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
    }
}
