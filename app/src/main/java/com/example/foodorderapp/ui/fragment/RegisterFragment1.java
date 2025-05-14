package com.example.foodorderapp.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodorderapp.databinding.FragmentRegister1Binding;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.HomeActivity;

/**
 * Fragment for completing user profile information after authentication
 */
public class RegisterFragment1 extends Fragment {

    private static final String TAG = "RegisterFragment1";

    private FragmentRegister1Binding binding;
    private UserService userService;

    public RegisterFragment1() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegister1Binding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        userService = new UserService();

        // Initialize user data if available
        loadUserData();

        // Set up continue button
        binding.btnContinue.setOnClickListener(v -> validateAndSaveUserData());
    }

    /**
     * Loads existing user data to pre-populate fields if available
     */
    private void loadUserData() {
        showLoading(true);
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    showLoading(false);
                    if (documentSnapshot.exists()) {
                        UserModel user = documentSnapshot.toObject(UserModel.class);
                        if (user != null) {
                            // Pre-populate fields with existing data
                            if (!TextUtils.isEmpty(user.getFullName())) {
                                binding.edtFullName.setText(user.getFullName());
                            }
                            if (!TextUtils.isEmpty(user.getPhone())) {
                                binding.edtPhone.setText(user.getPhone());
                            }
                            if (!TextUtils.isEmpty(user.getAddress())) {
                                binding.edtAddress.setText(user.getAddress());
                            }
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    Log.e(TAG, "Error loading user data", e);
                });
    }

    /**
     * Validates input fields and saves user data if valid
     */
    private void validateAndSaveUserData() {
        // Get input values and trim whitespace
        String fullName = binding.edtFullName.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String address = binding.edtAddress.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(fullName)) {
            binding.edtFullName.setError("Vui lòng nhập họ tên");
            binding.edtFullName.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(phone)) {
            binding.edtPhone.setError("Vui lòng nhập số điện thoại");
            binding.edtPhone.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(address)) {
            binding.edtAddress.setError("Vui lòng nhập địa chỉ");
            binding.edtAddress.requestFocus();
            return;
        }

        // Save user data
        saveUserData(fullName, phone, address);
    }

    /**
     * Saves validated user profile data to Firestore
     */
    private void saveUserData(String fullName, String phone, String address) {
        showLoading(true);

        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    UserModel user;
                    if (documentSnapshot.exists()) {
                        user = documentSnapshot.toObject(UserModel.class);
                    } else {
                        user = new UserModel();
                    }

                    if (user != null) {
                        // Update user model with form data
                        user.setFullName(fullName);
                        user.setPhone(phone);
                        user.setAddress(address);

                        // Save to Firestore
                        userService.updateUser(user)
                                .addOnSuccessListener(aVoid -> {
                                    showLoading(false);
                                    navigateToHome();
                                })
                                .addOnFailureListener(e -> {
                                    showLoading(false);
                                    showError("Không thể lưu thông tin: " + e.getMessage());
                                    Log.e(TAG, "Error updating user", e);
                                });
                    } else {
                        showLoading(false);
                        showError("Không thể cập nhật thông tin người dùng");
                    }
                })
                .addOnFailureListener(e -> {
                    showLoading(false);
                    showError("Lỗi khi tải thông tin người dùng");
                    Log.e(TAG, "Error getting user", e);
                });
    }

    /**
     * Navigates to the Home Activity
     */
    private void navigateToHome() {
        Intent intent = new Intent(getActivity(), HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Shows loading indicator
     */
    private void showLoading(boolean isLoading) {
        if (binding != null) {
            binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            binding.btnContinue.setEnabled(!isLoading);
        }
    }

    /**
     * Shows error message
     */
    private void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}