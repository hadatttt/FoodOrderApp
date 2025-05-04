package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.daos.ReviewDao;
import dut.com.fastfooddatabase.data.models.Review;

public class ReviewRepository {
    private final ReviewDao reviewDao = new ReviewDao();

    public void addReview(Review review, OnCompleteListener<Void> listener) {
        reviewDao.addReview(review).addOnCompleteListener(listener);
    }

    public void getReviewsByShop(String restaurantId, OnCompleteListener<QuerySnapshot> listener) {
        reviewDao.getReviewsByRestaurant(restaurantId).addOnCompleteListener(listener);
    }

    public void getReviewById(String reviewId, OnCompleteListener<DocumentSnapshot> listener) {
        reviewDao.getReviewById(reviewId).addOnCompleteListener(listener);
    }
}
