package com.example.foodorderapp.service;

import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.SearchQueryModel;
import com.example.foodorderapp.model.SearchResultModel;
import com.example.foodorderapp.model.ShopModel;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryService {
    private FirebaseFirestore db;

    public SearchQueryService() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<Void> addOrUpdateSearchQuery(SearchQueryModel query) {
        CollectionReference collection = db.collection("searchQueries");

        return collection
                .whereEqualTo("userId", query.getUserId())
                .whereEqualTo("keyword", query.getKeyword())
                .get()
                .continueWithTask(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (!snapshot.isEmpty()) {
                            // Nếu đã có, update timestamp bản ghi đầu tiên
                            DocumentReference docRef = snapshot.getDocuments().get(0).getReference();
                            return docRef.update("timestamp", query.getTimestamp());
                        } else {
                            // Nếu chưa có, thêm mới
                            return collection.add(query).continueWith(t -> null);
                        }
                    } else {
                        throw task.getException();
                    }
                });
    }

    public Task<QuerySnapshot> getAllSearchQueries() {
        return db.collection("searchQueries").get();
    }

    public Task<QuerySnapshot> getSearchQueryByUserId(String userId) {
        return db.collection("searchQueries")
                .whereEqualTo("userId", userId)
                .limit(10)
                .get();
    }

    public Task<Void> deleteAllSearchQueryByUserId(String userId) {
        CollectionReference collection = db.collection("searchQueries");
        return collection.whereEqualTo("userId", userId).get()
                .onSuccessTask(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentReference docRef : getDocumentReferences(querySnapshot)) {
                        batch.delete(docRef);
                    }
                    return batch.commit();
                });
    }

    private List<DocumentReference> getDocumentReferences(QuerySnapshot snapshot) {
        List<DocumentReference> refs = new ArrayList<>();
        snapshot.getDocuments().forEach(doc -> refs.add(doc.getReference()));
        return refs;
    }

    public Task<Void> deleteSearchQueryByKeywordAndUserId(String userId, String keyword) {
        CollectionReference collection = db.collection("searchQueries");

        return collection
                .whereEqualTo("userId", userId)
                .whereEqualTo("keyword", keyword)
                .get()
                .onSuccessTask(querySnapshot -> {
                    WriteBatch batch = db.batch();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        batch.delete(document.getReference());
                    }
                    return batch.commit();
                });
    }
    public Task<QuerySnapshot> getTopShopsByRating(int limit) {
        return db.collection("shops") // collection tên shops
                .orderBy("rating", Query.Direction.DESCENDING) // sắp xếp rating giảm dần
                .limit(limit)
                .get();
    }

    // Lấy top N foods theo rating giảm dần
    public Task<QuerySnapshot> getTopFoodsByRating(int limit) {
        return db.collection("foods") // collection tên foods
                .orderBy("rating", Query.Direction.DESCENDING)
                .limit(limit)
                .get();
    }

    public Task<List<SearchResultModel>> searchFoodsAndShopsByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) {
            return Tasks.forResult(new ArrayList<>());
        }

        Task<QuerySnapshot> foodTask = db.collection("foods")
                .orderBy("name")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .limit(20)
                .get();

        Task<QuerySnapshot> shopTask = db.collection("shops")
                .orderBy("shopName")
                .startAt(keyword)
                .endAt(keyword + "\uf8ff")
                .limit(20)
                .get();

        return Tasks.whenAllSuccess(foodTask, shopTask)
                .onSuccessTask(results -> {
                    List<SearchResultModel> searchResults = new ArrayList<>();

                    QuerySnapshot foodSnapshot = (QuerySnapshot) results.get(0);
                    for (DocumentSnapshot doc : foodSnapshot.getDocuments()) {
                        FoodModel food = doc.toObject(FoodModel.class);
                        if (food != null) {
                            searchResults.add(new SearchResultModel("food", food, null));
                        }
                    }

                    QuerySnapshot shopSnapshot = (QuerySnapshot) results.get(1);
                    for (DocumentSnapshot doc : shopSnapshot.getDocuments()) {
                        ShopModel shop = doc.toObject(ShopModel.class);
                        if (shop != null) {
                            searchResults.add(new SearchResultModel("shop", null, shop));
                        }
                    }

                    return Tasks.forResult(searchResults);
                });
    }
}
