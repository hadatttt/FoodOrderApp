package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.Review;

public class ReviewDao {
    private final CollectionReference reviewsRef;

    public ReviewDao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        reviewsRef = db.collection("reviews");
    }

    public Task<Void> addReview(Review review) {
        return reviewsRef.document(review.getId()).set(review);
    }

    public Task<QuerySnapshot> getReviewsByRestaurant(String restaurantId) {
        return reviewsRef.whereEqualTo("shopId", restaurantId).get();
    }

    public Task<DocumentSnapshot> getReviewById(String reviewId) {
        return reviewsRef.document(reviewId).get();
    }
}
