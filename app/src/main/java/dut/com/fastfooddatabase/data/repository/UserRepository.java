package dut.com.fastfooddatabase.data.repository;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.DocumentSnapshot;

import dut.com.fastfooddatabase.data.daos.UserDao;
import dut.com.fastfooddatabase.data.models.User;

public class UserRepository {
    private final UserDao userDao = new UserDao();

    public void createUser(User user, OnCompleteListener<Void> listener) {
        userDao.addUser(user).addOnCompleteListener(listener);
    }

    public void getUser(String userId, OnCompleteListener<DocumentSnapshot> listener) {
        userDao.getUserById(userId).addOnCompleteListener(listener);
    }

    public void updateUser(User user, OnCompleteListener<Void> listener) {
        userDao.updateUser(user).addOnCompleteListener(listener);
    }

    public void deleteUser(String userId, OnCompleteListener<Void> listener) {
        userDao.deleteUser(userId).addOnCompleteListener(listener);
    }
}
