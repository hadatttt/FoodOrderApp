package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.daos.SearchHistoryDao;
import dut.com.fastfooddatabase.data.models.SearchHistory;

public class SearchHistoryRepository {
    private final SearchHistoryDao searchHistoryDao;

    public SearchHistoryRepository() {
        this.searchHistoryDao = new SearchHistoryDao();
    }

    public void addSearchHistory(SearchHistory searchHistory, OnCompleteListener<Void> listener) {
        searchHistoryDao.addSearchHistory(searchHistory).addOnCompleteListener(listener);
    }

    public void getSearchHistoriesByUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        searchHistoryDao.getSearchHistoryByUserId(userId).addOnCompleteListener(listener);
    }
}
