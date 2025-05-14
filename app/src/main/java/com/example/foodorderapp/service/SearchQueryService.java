package com.example.foodorderapp.service;

import com.example.foodorderapp.model.SearchQueryModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;

import java.util.ArrayList;
import java.util.List;

public class SearchQueryService {
    private FirebaseFirestore db;

    public SearchQueryService() {
        db = FirebaseFirestore.getInstance();
    }

    public Task<DocumentReference> addSearchQuery(SearchQueryModel query) {
        CollectionReference searchQueryCollection = db.collection("searchQueries");
        return searchQueryCollection.add(query);
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

}
