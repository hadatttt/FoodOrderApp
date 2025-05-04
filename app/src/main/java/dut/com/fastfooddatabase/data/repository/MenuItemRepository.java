package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.daos.MenuItemDao;
import dut.com.fastfooddatabase.data.models.MenuItem;

public class MenuItemRepository {
    private final MenuItemDao menuItemDao = new MenuItemDao();

    public void addMenuItem(MenuItem item, OnCompleteListener<Void> listener) {
        menuItemDao.addMenuItem(item).addOnCompleteListener(listener);
    }

    public void getMenuItemsByShop(String restaurantId, OnCompleteListener<QuerySnapshot> listener) {
        menuItemDao.getMenuItemsByShop(restaurantId).addOnCompleteListener(listener);
    }

    public void getRandomMenuItems(int numOfItems, OnCompleteListener<QuerySnapshot> listener) {
        menuItemDao.getRandomMenuItems(numOfItems).addOnCompleteListener(listener);
    }


    public void getMenuItemById(String itemId, OnCompleteListener<DocumentSnapshot> listener) {
        menuItemDao.getMenuItemById(itemId).addOnCompleteListener(listener);
    }

    public void getMenuItemsByCategory(String category, OnCompleteListener<QuerySnapshot> listener) {
        menuItemDao.getMenuItemsByCategory(category).addOnCompleteListener(listener);
    }

    public void getMenuItemsByRestaurantAndCategory(String restaurantId, String category, OnCompleteListener<QuerySnapshot> listener) {
        menuItemDao.getMenuItemsByShopAndCategory(restaurantId, category).addOnCompleteListener(listener);
    }

    public void deleteMenuItem(String itemId, OnCompleteListener<Void> listener) {
        menuItemDao.deleteMenuItem(itemId).addOnCompleteListener(listener);
    }

    public void updateMenuItem(MenuItem item, OnCompleteListener<Void> listener) {
        menuItemDao.updateMenuItem(item).addOnCompleteListener(listener);
    }

    public void searchMenuItemsByName(String query, OnCompleteListener<QuerySnapshot> listener) {
        menuItemDao.getMenuItemByName(query).addOnCompleteListener(listener);
    }
}
