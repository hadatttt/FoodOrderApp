package dut.com.fastfooddatabase.data.daos;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import dut.com.fastfooddatabase.data.models.User;

public class UserDao {
    private final CollectionReference usersRef;

    public UserDao() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usersRef = db.collection("users");
    }

    public Task<Void> addUser(User user) {
        return usersRef.document(user.getId()).set(user);
    }

    public Task<DocumentSnapshot> getUserById(String userId) {
        return usersRef.document(userId).get();
    }

    public Task<Void> updateUser(User user) {
        return usersRef.document(user.getId()).set(user, SetOptions.merge());
    }

    public Task<Void> deleteUser(String userId) {
        return usersRef.document(userId).delete();
    }
}
