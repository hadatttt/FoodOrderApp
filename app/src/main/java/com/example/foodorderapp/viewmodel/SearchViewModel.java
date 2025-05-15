package com.example.foodorderapp.viewmodel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.SearchQueryModel;
import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.SearchQueryService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class SearchViewModel extends ViewModel {

    public class SearchResultItem {
        private int shopId;
        private int foodId;
        private String name;
        private String imageUrl;
        private double rating;

        public SearchResultItem(int shopId, int foodId, String name, String imageUrl, double rating) {
            this.shopId = shopId;
            this.foodId = foodId;
            this.name = name;
            this.imageUrl = imageUrl;
            this.rating = rating;
        }

        public String getName() {
            return name;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public double getRating() {
            return rating;
        }

        public int getShopId() {
            return shopId;
        }

        public int getFoodId() {
            return foodId;
        }
    }

    private final String userId;
    private final FoodService foodService = new FoodService();
    private final UserService userService = new UserService();
    private final ShopService shopService = new ShopService();
    private final SearchQueryService searchQueryService = new SearchQueryService();
    private final MutableLiveData<List<SearchResultItem>> searchResults = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<List<SearchQueryModel>> searchQueries = new MutableLiveData<>();
    private final MutableLiveData<List<FoodModel>> suggestions = new MutableLiveData<>();
    private final MutableLiveData<List<ShopModel>> hotShops = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> hasResults = new MutableLiveData<>(true);

    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    public SearchViewModel() {
        this.userId = userService.getUserId();
        loadSuggestions();
        loadHotShops();
        loadHistory();
    }

    public MutableLiveData<List<FoodModel>> getSuggestions() {
        return suggestions;
    }

    public MutableLiveData<List<ShopModel>> getHotShops() {
        return hotShops;
    }

    public MutableLiveData<List<SearchQueryModel>> getSearchQueries() {
        return searchQueries;
    }

    public MutableLiveData<List<SearchResultItem>> getSearchResults() {
        return searchResults;
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Boolean> getHasResults() {
        return hasResults;
    }

    public void addToSearchHistory(SearchQueryModel newQuery) {
        searchQueryService.addSearchQuery(newQuery);
    }

    private void loadHistory() {
        isLoading.setValue(true);
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        errorMessage.setValue("User document does not exist");
                        searchQueries.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                        return;
                    }

                    UserModel user = documentSnapshot.toObject(UserModel.class);
                    if (user == null) {
                        errorMessage.setValue("Invalid user data");
                        searchQueries.setValue(new ArrayList<>());
                        isLoading.setValue(false);
                        return;
                    }

                    searchQueryService.getSearchQueryByUserId(userId)
                            .addOnSuccessListener(querySnapshot -> {
                                List<SearchQueryModel> queries = querySnapshot != null
                                        ? querySnapshot.toObjects(SearchQueryModel.class)
                                        : new ArrayList<>();
                                searchQueries.setValue(queries);
                                isLoading.setValue(false);
                            })
                            .addOnFailureListener(e -> {
                                errorMessage.setValue("Failed to load search history: " + e.getMessage());
                                searchQueries.setValue(new ArrayList<>());
                                isLoading.setValue(false);
                            });
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to load user data: " + e.getMessage());
                    searchQueries.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
    }

    private void loadSuggestions() {
        isLoading.setValue(true);
        foodService.getLimitFoods(4)
                .addOnSuccessListener(snapshot -> {
                    List<FoodModel> foods = snapshot.toObjects(FoodModel.class);
                    for (  FoodModel food : foods) {
                        Log.d("SearchViewModel", "Food: " + food.getStoreId());
                    }
                    suggestions.setValue(foods);
                    hasResults.setValue(!foods.isEmpty());
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to load suggestions: " + e.getMessage());
                    suggestions.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
    }

    private void loadHotShops() {
        isLoading.setValue(true);
        shopService.getHotShops()
                .addOnSuccessListener(snapshot -> {
                    List<ShopModel> shops = snapshot.toObjects(ShopModel.class);
                    hotShops.setValue(shops);
                    hasResults.setValue(!shops.isEmpty());
                    isLoading.setValue(false);
                })
                .addOnFailureListener(e -> {
                    errorMessage.setValue("Failed to load hot shops: " + e.getMessage());
                    hotShops.setValue(new ArrayList<>());
                    isLoading.setValue(false);
                });
    }

    public void performSearch(String query) {
        if (query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            hasResults.setValue(true);
            isLoading.setValue(false);
            return;
        }

        isLoading.setValue(true);
        searchResults.setValue(new ArrayList<>());
        AtomicInteger taskCounter = new AtomicInteger(2); // Track food and shop searches

        executorService.execute(() -> {
            List<FoodModel> foods = foodService.getFoodsByName(query);
            processFoodResults(foods, taskCounter);
        });

        executorService.execute(() -> {
            List<ShopModel> shops = shopService.searchShopsByName(query);
            processShopResults(shops, taskCounter);
        });
    }

    private void processFoodResults(List<FoodModel> foodList, AtomicInteger taskCounter) {
        List<SearchResultItem> currentResults = new ArrayList<>(searchResults.getValue());
        if (foodList != null && !foodList.isEmpty()) {
            for (FoodModel item : foodList) {
                currentResults.add(new SearchResultItem(-1, item.getFoodId(), item.getName(), item.getImageUrl(), item.getRating()));
            }
            searchResults.postValue(currentResults);
            checkForResults();
        } else {
            errorMessage.postValue("No menu items found");
        }

        if (taskCounter.decrementAndGet() == 0) {
            isLoading.postValue(false);
        }
    }

    private void processShopResults(List<ShopModel> shopList, AtomicInteger taskCounter) {
        List<SearchResultItem> currentResults = new ArrayList<>(searchResults.getValue());
        if (shopList != null && !shopList.isEmpty()) {
            for (ShopModel item : shopList) {
                currentResults.add(new SearchResultItem(item.getStoreid(), -1, item.getShopName(), item.getImageUrl(), item.getRating()));
            }
            searchResults.postValue(currentResults);
            checkForResults();
        } else {
            errorMessage.postValue("No shops found");
        }

        if (taskCounter.decrementAndGet() == 0) {
            isLoading.postValue(false);
        }
    }

    private void checkForResults() {
        List<SearchResultItem> results = searchResults.getValue();
        hasResults.postValue(results != null && !results.isEmpty());
    }

    public void clearSearchQuery() {
        searchQueries.setValue(new ArrayList<>());
        searchQueryService.deleteAllSearchQueryByUserId(userId)
                .addOnFailureListener(e -> Log.e("SearchViewModel", "Failed to delete all history: " + e.getMessage()));
    }

    public void removeFromSearchHistory(SearchQueryModel query) {
        List<SearchQueryModel> currentHistory = searchQueries.getValue();
        if (currentHistory != null && currentHistory.remove(query)) {
            searchQueries.setValue(new ArrayList<>(currentHistory));
            searchQueryService.deleteSearchQueryByKeywordAndUserId(query.getUserId(), query.getKeyword())
                    .addOnFailureListener(e -> Log.e("SearchViewModel", "Failed to delete history: " + e.getMessage()));
        }
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        executorService.shutdown();
    }
}