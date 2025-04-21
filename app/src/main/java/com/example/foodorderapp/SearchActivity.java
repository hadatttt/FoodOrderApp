package com.example.foodorderapp;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.adapter.FavoritesAdapter;
import com.example.foodorderapp.adapter.HistoryAdapter;
import com.example.foodorderapp.adapter.SearchResultsAdapter;
import com.example.foodorderapp.adapter.SuggestionsAdapter;
import com.example.foodorderapp.model.FavoriteItem;
import com.example.foodorderapp.model.Restaurant;
import com.example.foodorderapp.model.SearchResult;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private List<String> historyList;
    private HistoryAdapter historyAdapter;
    private TextView txtEmptyHistory;
    private TextView noResultsTextView;
    private RecyclerView searchResultsRecyclerView;
    private RecyclerView historyRecyclerView;
    private RecyclerView suggestionsRecyclerView;
    private RecyclerView favoritesRecyclerView;
    private TextView historyTitle;
    private TextView suggestionsTitle;
    private TextView favoritesTitle;
    private ImageButton btnClearHistory;
    private List<Restaurant> restaurantList;
    private List<FavoriteItem> favoriteList;
    private List<SearchResult> searchResults;
    private SearchResultsAdapter searchResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize history list
        historyList = new ArrayList<>();
        historyList.add("Burger");
        historyList.add("Sandwich");
        historyList.add("Pizza");
        historyList.add("Khoai tây chiên");
        historyList.add("Mì cay");

        // Initialize restaurant list
        restaurantList = new ArrayList<>();
        restaurantList.add(new Restaurant("Pao Pao", 4.7f, R.drawable.restaurant_1));
        restaurantList.add(new Restaurant("The Food Hut", 4.5f, R.drawable.restaurant_2));
        restaurantList.add(new Restaurant("Papa's Pizza", 4.6f, R.drawable.restaurant_3));
        restaurantList.add(new Restaurant("Grill BBQ 168", 4.8f, R.drawable.restaurant_4));
        restaurantList.add(new Restaurant("Mr Chips", 4.4f, R.drawable.restaurant_5));

        // Initialize favorite list
        favoriteList = new ArrayList<>();
        favoriteList.add(new FavoriteItem("Gà Rán Và Khoai...", "Chicken Null", R.drawable.fried_chicken));
        favoriteList.add(new FavoriteItem("Buffalo Pizza", "Fratelli Figurato", R.drawable.pizza));
        favoriteList.add(new FavoriteItem("Burger Bò", "Lotteria", R.drawable.burger_beef));
        favoriteList.add(new FavoriteItem("Mì Cay", "Mì Cay Sasin", R.drawable.mi_cay));
        favoriteList.add(new FavoriteItem("Xúc Xích Nướng", "Ăn Vặt 5K", R.drawable.xuc_xich));
        favoriteList.add(new FavoriteItem("Gà Lắc Phô Mai", "Lotteria", R.drawable.ga_lac));
        favoriteList.add(new FavoriteItem("Pizza Pepperoni", "Pizza Hut", R.drawable.pepperoni_pizza));
        favoriteList.add(new FavoriteItem("Trà Sữa Trân Châu", "Bobapop", R.drawable.tra_sua));
        favoriteList.add(new FavoriteItem("Bánh Mì Thịt", "Bánh Mì Ông Tý", R.drawable.banh_mi));

        // Setup Search Results RecyclerView
        searchResultsRecyclerView = findViewById(R.id.search_results_recycler_view);
        searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchResults = new ArrayList<>();
        searchResultsAdapter = new SearchResultsAdapter(searchResults, searchResult -> {
            Toast.makeText(SearchActivity.this, "Clicked: " + searchResult.getName(), Toast.LENGTH_SHORT).show();
        });
        searchResultsRecyclerView.setAdapter(searchResultsAdapter);

        // Setup No Results TextView
        noResultsTextView = findViewById(R.id.no_results_text_view);

        // Setup History RecyclerView
        historyRecyclerView = findViewById(R.id.history_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);

        historyAdapter = new HistoryAdapter(
                historyList,
                historyItem -> {
                    Toast.makeText(SearchActivity.this, "Search for: " + historyItem, Toast.LENGTH_SHORT).show();
                    TextInputEditText searchInput = findViewById(R.id.search_input);
                    searchInput.setText(historyItem);
                },
                historyItem -> {
                    historyList.remove(historyItem);
                    historyAdapter.notifyDataSetChanged();
                    updateEmptyHistoryVisibility();
                }
        );
        historyRecyclerView.setAdapter(historyAdapter);

        // Setup Clear History Button
        btnClearHistory = findViewById(R.id.btnClearHistory);
        txtEmptyHistory = findViewById(R.id.txtEmptyHistory);
        btnClearHistory.setOnClickListener(v -> {
            historyList.clear();
            historyAdapter.notifyDataSetChanged();
            updateEmptyHistoryVisibility();
        });

        // Initial visibility check for empty history
        updateEmptyHistoryVisibility();

        // Setup Suggestions RecyclerView
        suggestionsRecyclerView = findViewById(R.id.suggestions_recycler_view);
        suggestionsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SuggestionsAdapter suggestionsAdapter = new SuggestionsAdapter(restaurantList, restaurant -> {
            Toast.makeText(SearchActivity.this, "Clicked: " + restaurant.getName(), Toast.LENGTH_SHORT).show();
        });
        suggestionsRecyclerView.setAdapter(suggestionsAdapter);

        // Setup Favorites RecyclerView
        favoritesRecyclerView = findViewById(R.id.favorites_recycler_view);
        favoritesRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        FavoritesAdapter favoritesAdapter = new FavoritesAdapter(favoriteList, favoriteItem -> {
            Toast.makeText(SearchActivity.this, "Clicked: " + favoriteItem.getName(), Toast.LENGTH_SHORT).show();
        });
        favoritesRecyclerView.setAdapter(favoritesAdapter);

        // Get references to section titles
        historyTitle = findViewById(R.id.history_title);
        suggestionsTitle = findViewById(R.id.suggestions_title);
        favoritesTitle = findViewById(R.id.favorites_title);

        // Setup Search Input Listener
        TextInputEditText searchInput = findViewById(R.id.search_input);
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String query = s.toString().trim().toLowerCase();
                Log.d("SearchActivity", "Query: " + query);
                performSearch(query);
            }
        });

        searchInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String query = searchInput.getText().toString().trim();
                if (!query.isEmpty() && !historyList.contains(query)) {
                    historyList.add(query);
                    historyAdapter.notifyDataSetChanged();
                    updateEmptyHistoryVisibility();
                }
                return true;
            }
            return false;
        });
    }

    private void performSearch(String query) {
        List<SearchResult> searchList = new ArrayList<>();

        if (query.isEmpty()) {
            // Hide search results and show other sections
            searchResultsRecyclerView.setVisibility(View.GONE);
            noResultsTextView.setVisibility(View.GONE);
            historyTitle.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.VISIBLE);
            btnClearHistory.setVisibility(View.VISIBLE);
            txtEmptyHistory.setVisibility(historyList.isEmpty() ? View.VISIBLE : View.GONE);
            suggestionsTitle.setVisibility(View.VISIBLE);
            suggestionsRecyclerView.setVisibility(View.VISIBLE);
            favoritesTitle.setVisibility(View.VISIBLE);
            favoritesRecyclerView.setVisibility(View.VISIBLE);
        } else {
            // Show search results and hide other sections
            historyTitle.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.GONE);
            btnClearHistory.setVisibility(View.GONE);
            txtEmptyHistory.setVisibility(View.GONE);
            suggestionsTitle.setVisibility(View.GONE);
            suggestionsRecyclerView.setVisibility(View.GONE);
            favoritesTitle.setVisibility(View.GONE);
            favoritesRecyclerView.setVisibility(View.GONE);

            // Search in restaurant list
            for (Restaurant restaurant : restaurantList) {
                if (restaurant.getName().toLowerCase().contains(query)) {
                    searchList.add(new SearchResult(
                            restaurant.getName(),
                            "Nhà hàng",
                            restaurant.getRating(),
                            restaurant.getImageRes()
                    ));
                }
            }

            // Search in favorite list
            for (FavoriteItem favorite : favoriteList) {
                if (favorite.getName().toLowerCase().contains(query)) {
                    searchList.add(new SearchResult(
                            favorite.getName(),
                            favorite.getRestaurant(),
                            -1,
                            favorite.getImageRes()
                    ));
                }
            }

            Log.d("SearchActivity", "Search results size: " + searchList.size());
            if (searchList.size() > 0)
                Log.d("SearchActivity", "" + searchList.get(0).getName());

            searchResultsAdapter.updateList(searchList);
            // Show search results or no results message
            if (searchList.isEmpty()) {
                searchResultsRecyclerView.setVisibility(View.GONE);
                noResultsTextView.setVisibility(View.VISIBLE);
            } else {
                searchResultsRecyclerView.setVisibility(View.VISIBLE);
                noResultsTextView.setVisibility(View.GONE);
            }
        }
    }

    private void updateEmptyHistoryVisibility() {
        if (historyList.isEmpty()) {
            txtEmptyHistory.setVisibility(View.VISIBLE);
            historyRecyclerView.setVisibility(View.GONE);
        } else {
            txtEmptyHistory.setVisibility(View.GONE);
            historyRecyclerView.setVisibility(View.VISIBLE);
        }
    }
}