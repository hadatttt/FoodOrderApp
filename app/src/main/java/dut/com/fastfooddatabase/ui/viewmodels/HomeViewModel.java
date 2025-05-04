package dut.com.fastfooddatabase.ui.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import java.util.List;
import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.data.repository.MenuItemRepository;
import dut.com.fastfooddatabase.data.repository.ShopRepository;

public class HomeViewModel extends ViewModel {
    private final MenuItemRepository menuItemRepository;
    private final ShopRepository shopRepository;

    private final MutableLiveData<List<MenuItem>> menuItems = new MutableLiveData<>();
    private final MutableLiveData<List<Shop>> shops = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public HomeViewModel() {
        ApplicationRepository appRepo = ApplicationRepository.getInstance();
        this.menuItemRepository = appRepo.getMenuItemRepository();
        this.shopRepository = appRepo.getShopRepository();
    }

    public LiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public LiveData<List<Shop>> getShops() {
        return shops;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadInitialData() {
        loadMenuItemsByCategory("All");
        loadFeaturedShops();
    }

    public void loadMenuItemsByCategory(String category) {
        isLoading.setValue(true);
        menuItemRepository.getMenuItemsByCategory(category, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<MenuItem> items = task.getResult().toObjects(MenuItem.class);
                menuItems.setValue(items);
            } else {
                errorMessage.setValue("Failed to load menu items: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }

    public void loadFeaturedShops() {
        isLoading.setValue(true);
        shopRepository.getRandomShops(5, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<Shop> shopList = task.getResult().toObjects(Shop.class);
                shops.setValue(shopList);
            } else {
                errorMessage.setValue("Failed to load shops: " +
                        (task.getException() != null ? task.getException().getMessage() : "Unknown error"));
            }
        });
    }
}