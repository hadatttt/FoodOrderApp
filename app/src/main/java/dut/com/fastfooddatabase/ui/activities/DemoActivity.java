package dut.com.fastfooddatabase.ui.activities;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import dut.com.fastfooddatabase.R;
import dut.com.fastfooddatabase.data.models.MenuItem;
import dut.com.fastfooddatabase.data.models.Shop;
import dut.com.fastfooddatabase.data.models.User;
import dut.com.fastfooddatabase.data.repository.MenuItemRepository;
import dut.com.fastfooddatabase.data.repository.ShopRepository;
import dut.com.fastfooddatabase.data.repository.UserRepository;

public class DemoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_demo);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        addUserSample();
        addShopSample();
        addMenuItemSample();
    }

    private void addUserSample() {
        UserRepository userRepository = new UserRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            User user = new User(
                    "" + currentIndex,
                    "User " + currentIndex,
                    "user" + currentIndex + "@example.com",
                    "password" + currentIndex,
                    "address" + currentIndex,
                    "customer"
            );
            userRepository.createUser(user, task -> {
                if (task.isSuccessful()) {
                    Log.d("UserRepo", "User " + currentIndex + " added successfully.");
                } else {
                    Log.e("UserRepo", "Error adding user " + currentIndex, task.getException());
                }
            });
        }
    }

    private void deleteUserSample() {
        UserRepository userRepository = new UserRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            userRepository.deleteUser("" + currentIndex, task -> {
                if (task.isSuccessful()) {
                    Log.d("UserRepo", "User " + currentIndex + " deleted successfully.");
                } else {
                    Log.e("UserRepo", "Error deleting user " + currentIndex, task.getException());
                }
            });
        }
    }

    private void addShopSample() {
        ShopRepository shopRepository = new ShopRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            Shop shop = new Shop(
                    "" + currentIndex,
                    "Shop " + currentIndex,
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.",
                    "Address " + currentIndex,
                    "0912345678",
                    4.5f,
                    "https://i.pinimg.com/736x/44/0f/9f/440f9ff9f9bbea3aa0f48f5cfcf7c79a.jpg"
            );
            shopRepository.addShop(shop, task -> {
                if (task.isSuccessful()) {
                    Log.d("ShopRepo", "Shop " + currentIndex + " added successfully.");
                } else {
                    Log.e("ShopRepo", "Error adding shop " + currentIndex, task.getException());
                }
            });
        }
    }

    private void deleteShopSample() {
        ShopRepository shopRepository = new ShopRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            shopRepository.deleteShop("" + i, task -> {
                if (task.isSuccessful()) {
                    Log.d("ShopRepo", "Shop " + currentIndex + " deleted successfully.");
                } else {
                    Log.e("ShopRepo", "Error deleting shop " + currentIndex, task.getException());
                }
            });
        }
    }

    private void addMenuItemSample() {
        // Implement this method to add menu items
        MenuItemRepository menuItemRepository = new MenuItemRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            MenuItem menuItem = new MenuItem(
                    "" + currentIndex,
                    "" + currentIndex,
                    "Burger " + currentIndex,
                    "Delicious burger with cheese and lettuce",
                    5.99f,
                    "https://i.pinimg.com/736x/d5/d4/bb/d5d4bb7e8a83e3cc20f3383e4ca3e5c7.jpg",
                    true,
                    "Burger",
                    0,
                    0
            );
            menuItemRepository.addMenuItem(menuItem, task -> {
                if (task.isSuccessful()) {
                    Log.d("MenuItemRepo", "Menu item " + currentIndex + " added successfully.");
                } else {
                    Log.e("MenuItemRepo", "Error adding menu item " + currentIndex, task.getException());
                }
            });
        }
    }

    private void deleteMenuItemSample() {
        // Implement this method to delete menu items
        MenuItemRepository menuItemRepository = new MenuItemRepository();
        for (int i = 1; i < 10; i++) {
            final int currentIndex = i;
            menuItemRepository.deleteMenuItem("" + currentIndex, task -> {
                if (task.isSuccessful()) {
                    Log.d("MenuItemRepo", "Menu item " + currentIndex + " deleted successfully.");
                } else {
                    Log.e("MenuItemRepo", "Error deleting menu item " + currentIndex, task.getException());
                }
            });
        }
    }
}