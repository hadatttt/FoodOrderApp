package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.SearchHistory;

public class SearchHistoryDao {
    private final CollectionReference historyRef;

    public SearchHistoryDao() {
        historyRef = FirebaseFirestore.getInstance().collection("search_history");
    }

    public Task<Void> addSearchHistory(SearchHistory searchHistory) {
        return historyRef.add(searchHistory).continueWith(task -> null);
    }

    public Task<QuerySnapshot> deleteAllHistoryByUserId(String userId) {
        return historyRef.whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }

    public Task<QuerySnapshot> getSearchHistoryByUserId(String userId) {
        return historyRef.whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(5)
                .get();
    }

    public Task<QuerySnapshot> deleteSearchHistoryByKeywordAndUserId(String userId,String keyword) {
        return historyRef.whereEqualTo("userId", userId)
                .whereEqualTo("keyword", keyword)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (var doc : querySnapshot.getDocuments()) {
                        doc.getReference().delete();
                    }
                });
    }
}
