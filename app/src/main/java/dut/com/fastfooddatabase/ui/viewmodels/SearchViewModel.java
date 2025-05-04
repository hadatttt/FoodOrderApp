package dut.com.fastfooddatabase.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.SearchHistory;
import dut.com.fastfooddatabase.data.models.SearchResultItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.data.repository.MenuItemRepository;
import dut.com.fastfooddatabase.data.repository.SearchHistoryRepository;
import dut.com.fastfooddatabase.data.repository.ShopRepository;

public class SearchViewModel extends ViewModel {
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;
    private final SearchHistoryRepository searchHistoryRepository;

    // LiveData for search results (combined MenuItem and Shop)
    private final MutableLiveData<List<SearchResultItem>> searchResults = new MutableLiveData<>(new ArrayList<>());

    // LiveData for popular/favorite items
    private final MutableLiveData<List<MenuItem>> favoriteItems = new MutableLiveData<>(new ArrayList<>());

    // LiveData for search suggestions
    private final MutableLiveData<List<Shop>> suggestions = new MutableLiveData<>();

    // LiveData for search history
    private final MutableLiveData<List<SearchHistory>> searchHistory = new MutableLiveData<>(new ArrayList<>());
    // Status indicators
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> hasResults = new MutableLiveData<>(true);

    public SearchViewModel() {
        ApplicationRepository appRepo = ApplicationRepository.getInstance();
        this.menuItemRepository = appRepo.getMenuItemRepository();
        this.shopRepository = appRepo.getShopRepository();
        this.searchHistoryRepository = appRepo.getSearchHistoryRepository();

        loadFavoriteItems();
        loadSearchHistoryItems();
        loadSuggestionsItems();
    }

    public LiveData<List<SearchResultItem>> getSearchResults() {
        return searchResults;
    }

    public LiveData<List<MenuItem>> getFavoriteItems() {
        return favoriteItems;
    }

    public LiveData<List<Shop>> getSuggestions() {
        return suggestions;
    }

    public LiveData<List<SearchHistory>> getSearchHistory() {
        return searchHistory;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public LiveData<Boolean> getHasResults() {
        return hasResults;
    }

    public void performSearch(String query) {
        if (query.trim().isEmpty()) {
            searchResults.setValue(new ArrayList<>());
            hasResults.setValue(true);
            return;
        }

        isLoading.setValue(true);
        hasResults.setValue(true);

        // Search for menu items
        menuItemRepository.searchMenuItemsByName(query, task -> processMenuItemResults(task, query));

        // Search for shops
        shopRepository.searchShopsByName(query, task -> processShopResults(task, query));
    }

    private void processMenuItemResults(com.google.android.gms.tasks.Task<QuerySnapshot> task, String query) {
        List<SearchResultItem> currentResults = searchResults.getValue() != null ? new ArrayList<>(searchResults.getValue()) : new ArrayList<>();
        if (task.isSuccessful() && task.getResult() != null) {
            List<MenuItem> items = task.getResult().toObjects(MenuItem.class);
            for (MenuItem item : items) {
                currentResults.add(new SearchResultItem(item.getName(), item.getImageUrl(), item.getRating()));
            }
            searchResults.setValue(currentResults);
            checkForResults();
        } else {
            errorMessage.setValue("Failed to search menu items: " +
                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            Log.e("SearchViewModel", "Error searching menu items: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
        }
        isLoading.setValue(false);
    }

    private void processShopResults(com.google.android.gms.tasks.Task<QuerySnapshot> task, String query) {
        List<SearchResultItem> currentResults = searchResults.getValue() != null ? new ArrayList<>(searchResults.getValue()) : new ArrayList<>();
        if (task.isSuccessful() && task.getResult() != null) {
            List<Shop> shops = task.getResult().toObjects(Shop.class);
            for (Shop shop : shops) {
                currentResults.add(new SearchResultItem(shop.getName(), shop.getImageUrl(), shop.getRating()));
            }
            searchResults.setValue(currentResults);
            checkForResults();
        } else {
            errorMessage.setValue("Failed to search shops: " +
                    (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            Log.e("SearchViewModel", "Error searching shops: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
        }
        isLoading.setValue(false);
    }

    private void checkForResults() {
        List<SearchResultItem> results = searchResults.getValue();
        boolean noResults = results == null || results.isEmpty();
        hasResults.setValue(!noResults);
    }

    public void loadFavoriteItems() {
        isLoading.setValue(true);
        menuItemRepository.getRandomMenuItems(8, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<MenuItem> items = task.getResult().toObjects(MenuItem.class);
                favoriteItems.setValue(items);
            } else {
                errorMessage.setValue("Failed to load favorite items: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Log.e("SearchViewModel", "Error loading favorite items: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    public void loadSuggestionsItems() {
        isLoading.setValue(true);
        shopRepository.getRandomShops(8, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<Shop> items = task.getResult().toObjects(Shop.class);
                suggestions.setValue(items);
            } else {
                errorMessage.setValue("Failed to load favorite items: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Log.e("SearchViewModel", "Error loading suggestions items: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    public void loadSearchHistoryItems() {
        isLoading.setValue(true);
        searchHistoryRepository.getSearchHistoriesByUser("0", task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<SearchHistory> items = task.getResult().toObjects(SearchHistory.class);
                searchHistory.setValue(items);
            } else {
                errorMessage.setValue("Failed to load favorite items: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                Log.e("SearchViewModel", "Error loading search history items: " + (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    public void addToSearchHistory(String query) {
        List<SearchHistory> currentHistory = searchHistory.getValue();
        if (currentHistory == null) {
            currentHistory = new ArrayList<>();
        }
        if (!currentHistory.contains(query)) {
            currentHistory.add(new SearchHistory("" + 0, query, new Date()));

            if (currentHistory.size() > 10) {
                currentHistory = currentHistory.subList(0, 10);
            }
            searchHistory.setValue(currentHistory);
        }
    }

    public void clearSearchHistory() {
        searchHistory.setValue(new ArrayList<>());

        // Delete from database
        searchHistoryRepository.deleteAllSearchHistoryByUserId("0", task -> {
            if (!task.isSuccessful()) {
                Log.e("SearchViewModel", "Failed to delete all history: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    public void removeFromSearchHistory(SearchHistory query) {
        List<SearchHistory> currentHistory = searchHistory.getValue();
        if (currentHistory != null && currentHistory.remove(query)) {
            searchHistory.setValue(new ArrayList<>(currentHistory));

            searchHistoryRepository.deleteSearchHistoryByKeywordAndUserId(query.getUserId(), query.getKeyword(), task -> {
                if (!task.isSuccessful()) {
                    Log.e("SearchViewModel", "Failed to delete history: " +
                            (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
                }
            });
        }
    }
}