package com.example.foodorderapp.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.ShopService;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final ShopService shopService;

    private final MutableLiveData<List<ShopModel>> hotShops = new MutableLiveData<>();
    private final MutableLiveData<List<ShopModel>> newShops = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);

    public HomeViewModel() {
        shopService = new ShopService(); // hoặc inject qua constructor nếu bạn dùng DI
    }

    public LiveData<List<ShopModel>> getHotShops() {
        return hotShops;
    }

    public LiveData<List<ShopModel>> getNewShops() {
        return newShops;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void loadInitialData() {
        loadHotShops();
        loadNewShops();
    }

    public void loadHotShops() {
        isLoading.setValue(true);
        shopService.getHotShops().addOnCompleteListener(task -> {
            isLoading.postValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<ShopModel> shopList = task.getResult().toObjects(ShopModel.class);
                hotShops.postValue(shopList);
            } else {
                hotShops.postValue(null);
            }
        });
    }

    public void loadNewShops() {
        isLoading.setValue(true);
        shopService.getAllShops().addOnCompleteListener(task -> {
            isLoading.postValue(false);
            if (task.isSuccessful() && task.getResult() != null) {
                List<ShopModel> shopList = task.getResult().toObjects(ShopModel.class);
                newShops.postValue(shopList.subList(0, Math.min(30, shopList.size())));
            } else {
                newShops.postValue(null);
            }
        });
    }
}
