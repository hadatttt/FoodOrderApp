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
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.SearchHotFoodAdapter;
import com.example.foodorderapp.adapter.SearchHotShopAdapter;
import com.example.foodorderapp.adapter.SearchResultAdapter;
import com.example.foodorderapp.adapter.SearchSearchQueryAdapter;
import com.example.foodorderapp.databinding.ActivitySearchBinding;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.SearchQueryModel;
import com.example.foodorderapp.model.SearchResultModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.SearchQueryService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";

    private UserService userService = new UserService();
    private SearchQueryService searchQueryService;

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
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        searchQueryService = new SearchQueryService();

        setupListeners();

        userService.getUser()
                .addOnSuccessListener(this::onUserLoaded)
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to get user data", e);
                    goToLogin();
                });
    }

    private void onUserLoaded(DocumentSnapshot documentSnapshot) {
        if (documentSnapshot.exists()) {
            UserModel user = documentSnapshot.toObject(UserModel.class);
            initAdapters();
            loadSearchHistory();
            loadHotShopsAndFoods();
        } else {
            Log.d(TAG, "User document does not exist.");
            goToLogin();
        }
    }

    private void initAdapters() {
        searchQueryAdapter = new SearchSearchQueryAdapter(
                new ArrayList<>(),
                searchQuery -> {
                    binding.searchInput.setText(searchQuery.getKeyword());
                    performSearch(searchQuery.getKeyword());
                },
                searchQueryService,
                userService.getUserId()
        );

        searchResultAdapter = new SearchResultAdapter(item -> {
            String keyword = binding.searchInput.getText().toString().trim();
            if (!keyword.isEmpty()) {
                saveSearchQuery(keyword);
            }
            if (item.getStoreId() != -1) {
                Intent intent = new Intent(this, DetailShopActivity.class);
                intent.putExtra("storeId", item.getStoreId());
                if (item.getFoodId() != -1) {
                    intent.putExtra("foodId", item.getFoodId());
                }
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
    }

    private void setupListeners() {
        binding.searchInput.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String keyword = s.toString().trim();
                if (!keyword.isEmpty()) {
                    performSearch(keyword);
                } else {
                    performSearch("");
                }
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {}
        });

        binding.searchClearHistoryBtn.setOnClickListener(v -> clearSearchHistory());

        binding.btnBack.setOnClickListener(v -> finish());
    }

    private void saveSearchQuery(String keyword) {
        SearchQueryModel newQuery = new SearchQueryModel(
                userService.getUserId(),
                keyword,
                new Date()
        );

        searchQueryService.addOrUpdateSearchQuery(newQuery)
                .addOnSuccessListener(aVoid -> loadSearchHistory())
                .addOnFailureListener(e -> Toast.makeText(this, "Lỗi lưu lịch sử: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadSearchHistory() {
        searchQueryService.getSearchQueryByUserId(userService.getUserId())
                .addOnSuccessListener(querySnapshot -> {
                    List<SearchQueryModel> searchQueries = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        SearchQueryModel query = doc.toObject(SearchQueryModel.class);
                        if (query != null) searchQueries.add(query);
                    }
                    searchQueryAdapter.updateItems(searchQueries);
                    updateEmptyHistoryVisibility(searchQueries);
                    showDefaultUI();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Lỗi tải lịch sử tìm kiếm: ", e);
                    updateEmptyHistoryVisibility(null);
                });
    }

    private void clearSearchHistory() {
        searchQueryService.deleteAllSearchQueryByUserId(userService.getUserId())
                .addOnSuccessListener(aVoid -> {
                    loadSearchHistory();
//                    Toast.makeText(this, "Đã xóa lịch sử tìm kiếm", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Xóa lịch sử thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void performSearch(String keyword) {
        if (keyword.isEmpty()) {
            showDefaultUI();
            loadSearchHistory();
            loadHotShopsAndFoods();
            searchResultAdapter.updateResults(new ArrayList<>());
            binding.noResultsTextView.setVisibility(View.GONE);
            return;
        }

        // Nếu từ khóa không rỗng, tiến hành tìm kiếm bình thường
        searchQueryService.searchByKeyword(keyword)
                .addOnSuccessListener(querySnapshot -> {
                    List<FoodModel> results = new ArrayList<>();
                    for (DocumentSnapshot doc : querySnapshot.getDocuments()) {
                        FoodModel item = doc.toObject(FoodModel.class);
                        if (item != null) results.add(item);
                    }
                    searchResultAdapter.updateResults(results);

                    // Ẩn phần gợi ý và yêu thích
                    showSearchResultsUI();

                    // Nếu không có kết quả, hiển thị thông báo
                    if (results.isEmpty()) {
                        binding.noResultsTextView.setVisibility(View.VISIBLE);
                    } else {
                        binding.noResultsTextView.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi tìm kiếm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void loadHotShopsAndFoods() {
        searchQueryService.getTopShopsByRating(5)
                .addOnSuccessListener(shopSnapshots -> {
                    List<ShopModel> hotShops = new ArrayList<>();
                    for (DocumentSnapshot doc : shopSnapshots.getDocuments()) {
                        ShopModel shop = doc.toObject(ShopModel.class);
                        if (shop != null) hotShops.add(shop);
                    }
                    searchHotShopAdapter.updateItems(hotShops);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load hot shops", e));

        searchQueryService.getTopFoodsByRating(4)
                .addOnSuccessListener(foodSnapshots -> {
                    List<FoodModel> hotFoods = new ArrayList<>();
                    for (DocumentSnapshot doc : foodSnapshots.getDocuments()) {
                        FoodModel food = doc.toObject(FoodModel.class);
                        if (food != null) hotFoods.add(food);
                    }
                    searchHotFoodAdapter.updateItems(hotFoods);
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to load hot foods", e));
    }


    private void updateEmptyHistoryVisibility(List<SearchQueryModel> historyItems) {
        binding.txtEmptyHistory.setVisibility(
                (historyItems == null || historyItems.isEmpty()) ? View.VISIBLE : View.GONE
        );
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

        binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
        binding.noResultsTextView.setVisibility(View.GONE);
        binding.btnBack.setVisibility(View.VISIBLE);
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}
