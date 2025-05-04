package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.daos.ShopDao;
import dut.com.fastfooddatabase.data.models.Shop;

public class ShopRepository {
    private final ShopDao shopDao = new ShopDao();

    public void addShop(Shop shop, OnCompleteListener<Void> listener) {
        shopDao.addShop(shop).addOnCompleteListener(listener);
    }

    public void getShopById(String id, OnCompleteListener<DocumentSnapshot> listener) {
        shopDao.getShopById(id).addOnCompleteListener(listener);
    }

    public void getAllShops(OnCompleteListener<QuerySnapshot> listener) {
        shopDao.getAllShops().addOnCompleteListener(listener);
    }

    public void getRandomShops(int numOfShops, OnCompleteListener<QuerySnapshot> listener) {
        shopDao.getRandomShops(numOfShops).addOnCompleteListener(listener);
    }

    public void deleteShop(String id, OnCompleteListener<Void> listener) {
        shopDao.deleteShop(id).addOnCompleteListener(listener);
    }
    public void updateShop(Shop shop, OnCompleteListener<Void> listener) {
        shopDao.updateShop(shop).addOnCompleteListener(listener);
    }

    public void searchShopsByName(String query, OnCompleteListener<QuerySnapshot> listener) {
        shopDao.getShopsByName(query).addOnCompleteListener(listener);
    }
}
