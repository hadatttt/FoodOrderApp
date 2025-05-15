package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
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
    private final String TAG = "Search Activity";
    private UserService userService = new UserService();

    private SearchViewModel searchViewModel;
    private SearchSearchQueryAdapter searchQueryAdapter;
    private SearchResultAdapter searchResultAdapter;
    private SearchHotShopAdapter searchHotShopAdapter;
    private SearchHotFoodAdapter searchHotFoodAdapter;

    private ActivitySearchBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.searchInput.setOnEditorActionListener((textView, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN)) {

                String keyword = binding.searchInput.getText().toString().trim();

                if (!keyword.isEmpty()) {
                    // Thêm vào lịch sử
                    List<SearchQueryModel> searchQueries = searchViewModel.getSearchQueries().getValue();
                    if (searchQueries == null) {
                        searchQueries = new ArrayList<>();
                    }

                    SearchQueryModel newQuery = new SearchQueryModel(userService.getUserId(), keyword, new Date());
                    searchQueries.add(0, newQuery); // thêm đầu danh sách để hiển thị mới nhất lên trước
                    searchViewModel.getSearchQueries().setValue(searchQueries);

                    // Gọi tìm kiếm
                    searchViewModel.performSearch(keyword);

                    // Ẩn bàn phím nếu cần
                    textView.clearFocus();
                }

                return true;
            }
            return false;
        });

        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                UserModel user = documentSnapshot.toObject(UserModel.class);
                loadData();
            } else {
                Log.d(TAG, "User document does not exist.");
                return;
            }
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Failed to get user data", e);
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            return;
        });
    }
    private void loadData() {
        searchViewModel = new ViewModelProvider(this, new SavedStateViewModelFactory(getApplication(), this)).get(SearchViewModel.class);
        searchQueryAdapter = new SearchSearchQueryAdapter(new ArrayList<>(), searchQuery -> {
            binding.searchInput.setText(searchQuery.getKeyword());
            searchViewModel.performSearch(searchQuery.getKeyword());
        }, searchQuery -> {
            searchViewModel.removeFromSearchHistory(searchQuery);
        });
        searchResultAdapter = new SearchResultAdapter(item -> {
            Toast.makeText(this, "Clicked: " + item.getName(), Toast.LENGTH_SHORT).show();
            if (item.getShopId() != -1) {
                // Navigate to shop detail page
                Intent intent = new Intent(this, DetailShopActivity.class);
                intent.putExtra("storeId", item.getShopId());
                startActivity(intent);
            } else if (item.getFoodId() != -1) {
                // Navigate to food detail page
                Intent intent = new Intent(this, DetailFoodActivity.class);
                intent.putExtra("foodId", item.getFoodId());
                startActivity(intent);
            }
        });
        searchHotShopAdapter = new SearchHotShopAdapter(new ArrayList<>(),
                shop -> {
                    Intent intent = new Intent(this, DetailShopActivity.class);
                    intent.putExtra("storeId", shop.getStoreid());
                    startActivity(intent);
                });
        searchHotFoodAdapter = new SearchHotFoodAdapter(new ArrayList<>(),
                food -> {
                    Intent intent = new Intent(this, DetailFoodActivity.class);
                    intent.putExtra("foodId", food.getFoodId());
                    startActivity(intent);
                });

        binding.searchHistoryRecyclerView.setAdapter(searchQueryAdapter);
        binding.searchResultsRecyclerView.setAdapter(searchResultAdapter);
        binding.searchSuggestionsRecyclerView.setAdapter(searchHotShopAdapter);
        binding.searchFavoritesRecyclerView.setAdapter(searchHotFoodAdapter);

        // Load data into adapters
        searchViewModel.getSearchQueries().observe(this, searchQueries -> {
            searchQueryAdapter.updateItems(searchQueries);
            updateEmptyHistoryVisibility(searchQueries);
        });

        searchViewModel.getSearchResults().observe(this, searchResults -> {
            searchResultAdapter.updateResults(searchResults);
            updateSearchResultsVisibility();

        });

        searchViewModel.getHotShops().observe(this, hotShops -> {
            Log.d("SearchActivity", "Hot shops loaded: " + hotShops.size());
            searchHotShopAdapter.updateItems(hotShops);
        });

        searchViewModel.getSuggestions().observe(this, hotFoods -> {
            Log.d("SearchActivity", "Suggestions loaded: " + hotFoods.size());
            searchHotFoodAdapter.updateItems(hotFoods);
        });

        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    private void updateSearchResultsVisibility() {
        boolean hasResults = searchViewModel.getHasResults().getValue() != null && searchViewModel.getHasResults().getValue();
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
        binding.searchHistoryTitle.setVisibility(View.VISIBLE);
        binding.searchHistoryRecyclerView.setVisibility(View.VISIBLE);
        binding.searchClearHistoryBtn.setVisibility(View.VISIBLE);
        binding.searchSuggestionsRecyclerView.setVisibility(View.VISIBLE);
        binding.searchSuggestionsTitle.setVisibility(View.VISIBLE);
        binding.searchFavoritesTitle.setVisibility(View.VISIBLE);
        binding.searchFavoritesRecyclerView.setVisibility(View.VISIBLE);
        binding.btnBack.setVisibility(View.VISIBLE);
    }

    private void showSearchResultsUI() {
        binding.searchHistoryTitle.setVisibility(View.GONE);
        binding.searchHistoryRecyclerView.setVisibility(View.GONE);
        binding.searchClearHistoryBtn.setVisibility(View.GONE);
        binding.txtEmptyHistory.setVisibility(View.GONE);
        binding.searchSuggestionsTitle.setVisibility(View.GONE);
        binding.searchSuggestionsRecyclerView.setVisibility(View.GONE);
        binding.searchFavoritesTitle.setVisibility(View.GONE);
        binding.searchFavoritesRecyclerView.setVisibility(View.GONE);
        binding.btnBack.setVisibility(View.VISIBLE);
    }

    private void updateEmptyHistoryVisibility(List<SearchQueryModel> historyItems) {
        binding.txtEmptyHistory.setVisibility(
                historyItems == null || historyItems.isEmpty() ? View.VISIBLE : View.GONE
        );
    }
}