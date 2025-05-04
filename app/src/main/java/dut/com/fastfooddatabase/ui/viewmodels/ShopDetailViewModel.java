package dut.com.fastfooddatabase.ui.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Review;
import dut.com.fastfooddatabase.data.repository.ApplicationRepository;
import dut.com.fastfooddatabase.data.repository.MenuItemRepository;
import dut.com.fastfooddatabase.data.repository.ReviewRepository;

public class ShopDetailViewModel extends ViewModel {

    private String shopId = "0";
    private final MenuItemRepository menuItemRepository;
    private final ReviewRepository reviewRepository;
    private final MutableLiveData<List<MenuItem>> menuItems = new MutableLiveData<>();
    private final MutableLiveData<List<Review>> reviews = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public MutableLiveData<List<MenuItem>> getMenuItems() {
        return menuItems;
    }

    public MutableLiveData<List<Review>> getReviews() {
        return reviews;
    }

    public ShopDetailViewModel() {
        ApplicationRepository appRepo = ApplicationRepository.getInstance();
        this.menuItemRepository = appRepo.getMenuItemRepository();
        this.reviewRepository = appRepo.getReviewRepository();

        loadReviews(shopId);
        loadMenuItems(shopId);
    }

    public void setShopId(String shopId) {
        this.shopId = shopId;
        loadMenuItems(shopId);
        loadReviews(shopId);
    }

    public void loadMenuItems(String shopId) {
        isLoading.setValue(true);
        menuItemRepository.getMenuItemsByShop(shopId, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                List<MenuItem> items = task.getResult().toObjects(MenuItem.class);
                Log.d("ShopDetailViewModel", "Menu items loaded successfully: " + items.size());
                menuItems.setValue(items);
            } else {
                errorMessage.setValue("Failed to load menu items");
                Log.e("ShopDetailViewModel", "Error loading menu items: " + task.getException());
            }
        });
    }

    public void loadReviews(String shopId) {
        isLoading.setValue(true);
        reviewRepository.getReviewsByShop(shopId, task -> {
            isLoading.setValue(false);
            if (task.isSuccessful()) {
                List<Review> reviewList = task.getResult().toObjects(Review.class);
                reviews.setValue(reviewList);
            } else {
                errorMessage.setValue("Failed to load reviews");
                Log.e("ShopDetailViewModel", "Error loading reviews: " + task.getException());
            }
        });
    }
}
