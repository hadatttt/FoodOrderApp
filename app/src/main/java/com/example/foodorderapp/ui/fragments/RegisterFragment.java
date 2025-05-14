package com.example.foodorderapp.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.FragmentRegisterBinding;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.HomeActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class RegisterFragment extends Fragment {

    private FragmentRegisterBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private UserService userService = new UserService();

    public RegisterFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Đăng ký tài khoản bằng email & password
        binding.btnRegister.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString().trim();
            String password = binding.edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                binding.tvRegisterError.setText("Vui lòng điền đầy đủ thông tin");
                binding.tvRegisterError.setVisibility(View.VISIBLE);
                return;
            }

            UserModel userModel = new UserModel();
            userModel.setEmail(email);

            userService.registerUser(email, password, userModel)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Tự động đăng nhập sau khi đăng ký thành công
                            userService.loginUser(email, password)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            binding.tvRegisterError.setVisibility(View.GONE);
                                            navigateToRegisterFragment1();
                                        } else {
                                            showError("Đăng nhập thất bại: " + task1.getException().getMessage());
                                        }
                                    });
                        } else {
                            showError("Đăng ký thất bại: " + task.getException().getMessage());
                        }
                    });
        });

        // Cấu hình đăng nhập bằng Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso);

        // Đăng nhập Google
        binding.btnLoginGoogle.setOnClickListener(v -> {
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null) {
                    showError("Không lấy được tài khoản Google");
                    return;
                }

                String idToken = account.getIdToken();
                String email = account.getEmail();

                userService.loginWithGoogle(idToken)
                        .addOnCompleteListener(requireActivity(), task1 -> {
                            if (task1.isSuccessful()) {
                                userService.getUserByEmail(email).addOnSuccessListener(documentSnapshot -> {
                                    if (!documentSnapshot.isEmpty()) {
                                        // Người dùng mới => sang màn hình nhập thông tin cá nhân
                                        navigateToRegisterFragment1();
                                    } else {
                                        UserModel userModel = documentSnapshot.getDocuments().get(0).toObject(UserModel.class);
                                        if (userModel != null && userModel.getFullName() != null && !userModel.getFullName().isEmpty()) {
                                            startActivity(new Intent(requireContext(), HomeActivity.class));
                                            requireActivity().finish();
                                        } else {
                                            // Người dùng chưa có fullName => cần bổ sung thông tin
                                            navigateToRegisterFragment1();
                                        }
                                    }
                                }).addOnFailureListener(e -> {
                                    Log.e("RegisterFragment", "Lỗi khi lấy thông tin người dùng", e);
                                    showError("Không kiểm tra được trạng thái người dùng");
                                });

                            } else {
                                showError("Đăng nhập Google thất bại");
                            }
                        });

            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
                showError("Đăng nhập Google thất bại");
            }
        }
    }

    private void navigateToRegisterFragment1() {
        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.register_fragment_container, new RegisterFragment1())
                .addToBackStack(null)
                .commit();
    }

    private void showError(String message) {
        binding.tvRegisterError.setText(message);
        binding.tvRegisterError.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
