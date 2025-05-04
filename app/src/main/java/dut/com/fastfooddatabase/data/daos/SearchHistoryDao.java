package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.SearchHistory;

public class SearchHistoryDao {
    private final CollectionReference historyRef;

    public SearchHistoryDao() {
        historyRef = FirebaseFirestore.getInstance().collection("search_history");
    }

    public Task<Void> addSearchHistory(SearchHistory searchHistory) {
        return historyRef.document(searchHistory.getUserId()).set(searchHistory);
    }
    public Task<Void> deleteSearchHistory(String userId) {
        return historyRef.document(userId).delete();
    }
    public Task<QuerySnapshot> getSearchHistoryByUserId(String userId) {
        return historyRef.whereEqualTo("userId", userId).limit(5).get();
    }

}
