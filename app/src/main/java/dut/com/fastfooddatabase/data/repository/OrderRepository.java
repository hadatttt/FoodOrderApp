package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import dut.com.fastfooddatabase.data.daos.OrderDao;
import dut.com.fastfooddatabase.data.models.Order;

public class OrderRepository {
    private final OrderDao orderDao = new OrderDao();

    public void placeOrder(Order order, OnCompleteListener<Void> listener) {
        orderDao.addOrder(order).addOnCompleteListener(listener);
    }

    public void getOrdersByUser(String userId, OnCompleteListener<QuerySnapshot> listener) {
        orderDao.getOrdersByUser(userId).addOnCompleteListener(listener);
    }

    public void getOrderById(String orderId, OnCompleteListener<DocumentSnapshot> listener) {
        orderDao.getOrderById(orderId).addOnCompleteListener(listener);
    }

    public void updateOrderStatus(String orderId, String status, OnCompleteListener<Void> listener) {
        orderDao.updateOrderStatus(orderId, status).addOnCompleteListener(listener);
    }

}
