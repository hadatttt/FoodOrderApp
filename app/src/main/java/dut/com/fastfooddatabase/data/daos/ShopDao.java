package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.Shop;

public class ShopDao {
    private final CollectionReference shopRef;

    public ShopDao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        shopRef = db.collection("shops");
    }

    public Task<Void> addShop(Shop shop) {
        return shopRef.document(shop.getId()).set(shop);
    }

    public Task<DocumentSnapshot> getShopById(String shopId) {
        return shopRef.document(shopId).get();
    }

    public Task<QuerySnapshot> getAllShops() {
        return shopRef.get();
    }

    public Task<QuerySnapshot> getRandomShops(int numOfShops) {
        return shopRef.limit(numOfShops).get();
    }

    public Task<Void> deleteShop(String shopId) {
        return shopRef.document(shopId).delete();
    }

    public Task<Void> updateShop(Shop shop) {
        return shopRef.document(shop.getId()).set(shop);
    }
}
