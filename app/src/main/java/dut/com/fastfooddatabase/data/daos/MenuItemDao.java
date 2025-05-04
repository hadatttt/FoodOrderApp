package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.MenuItem;

public class MenuItemDao {
    private final CollectionReference menuItemsRef;

    public MenuItemDao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        menuItemsRef = db.collection("menu_items");
    }

    public Task<Void> addMenuItem(MenuItem item) {
        return menuItemsRef.document(item.getId()).set(item);
    }

    public Task<Void> deleteMenuItem(String itemId) {
        return menuItemsRef.document(itemId).delete();
    }

    public Task<Void> updateMenuItem(MenuItem item) {
        return menuItemsRef.document(item.getId()).set(item);
    }

    public Task<QuerySnapshot> getRandomMenuItems(int numOfItems) {
        return menuItemsRef.limit(numOfItems).get();
    }

    public Task<QuerySnapshot> getMenuItemsByShop(String shopId) {
        return menuItemsRef.whereEqualTo("shopId", shopId).get();
    }

    public Task<DocumentSnapshot> getMenuItemById(String itemId) {
        return menuItemsRef.document(itemId).get();
    }

    public Task<QuerySnapshot> getMenuItemsByCategory(String category) {
        if (category == "all") {
            return menuItemsRef.get();
        }
        return menuItemsRef.whereEqualTo("category", category).get();
    }

    public Task<QuerySnapshot> getMenuItemsByShopAndCategory(String restaurantId, String category) {
        return menuItemsRef.whereEqualTo("shopId", restaurantId).whereEqualTo("category", category).get();
    }

    public Task<QuerySnapshot> getMenuItemByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return menuItemsRef.get();
        }

        String lowercaseQuery = query.trim().toLowerCase();
        return menuItemsRef
                .whereGreaterThanOrEqualTo("name_lowercase", lowercaseQuery)
                .whereLessThanOrEqualTo("name_lowercase", lowercaseQuery + "\uf8ff")
                .get();
    }
}
