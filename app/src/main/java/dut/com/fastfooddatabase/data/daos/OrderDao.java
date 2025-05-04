package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.models.Order;

public class OrderDao {
    private final CollectionReference ordersRef;

    public OrderDao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        ordersRef = db.collection("orders");
    }

    public Task<Void> addOrder(Order order) {
        return ordersRef.document(order.getId()).set(order);
    }

    public Task<QuerySnapshot> getOrdersByUser(String userId) {
        return ordersRef.whereEqualTo("userId", userId).get();
    }

    public Task<Void> updateOrderStatus(String orderId, String newStatus) {
        return ordersRef.document(orderId).update("status", newStatus);
    }

    public Task<DocumentSnapshot> getOrderById(String orderId) {
        return ordersRef.document(orderId).get();
    }
}
