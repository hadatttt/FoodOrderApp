package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.ActivityRegister1Binding;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.fragment.RegisterFragment;
import com.example.foodorderapp.ui.fragment.RegisterFragment1;

/**
 * Activity for completing user registration or profile information.
 * Handles both new registrations and existing users with incomplete profiles.
 */
public class RegisterActivity1 extends AppCompatActivity {

    private static final String TAG = "RegisterActivity1";

    private ActivityRegister1Binding binding;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize view binding
        binding = ActivityRegister1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize user service
        userService = new UserService();

        // Check user authentication state and determine appropriate flow
        checkUserState();
    }

    /**
     * Determines the appropriate registration flow based on user authentication state
     */
    private void checkUserState() {
        // First check if we have a currently authenticated user
        if (userService.getUser() == null) {
            // No user is signed in, show the initial registration screen
            loadRegisterFragment();
            return;
        }

        // User is signed in, check if they have a complete profile
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    if (!documentSnapshot.exists()) {
                        // User authenticated but no profile document exists
                        loadRegisterFragment1();
                    } else {
                        // Profile exists, check if it's complete
                        UserModel userModel = documentSnapshot.toObject(UserModel.class);
                        if (isIncompleteProfile(userModel)) {
                            // Profile exists but is incomplete
                            loadRegisterFragment1();
                        } else {
                            // Profile is complete, go to home
                            navigateToHome();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching user data", e);
                    // On failure to fetch user data, load initial registration
                    loadRegisterFragment();
                });
    }

    /**
     * Checks if user profile has missing required information
     */
    private boolean isIncompleteProfile(UserModel userModel) {
        return userModel == null || userModel.getFullName() == null || userModel.getFullName().isEmpty();
    }

    /**
     * Loads the initial registration fragment for new users
     */
    private void loadRegisterFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_fragment_container, new RegisterFragment())
                .commit();
    }

    /**
     * Loads the profile completion fragment for authenticated users
     */
    private void loadRegisterFragment1() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.register_fragment_container, new RegisterFragment1())
                .commit();
    }

    /**
     * Navigates to the home screen
     */
    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity1.this, HomeActivity1.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}