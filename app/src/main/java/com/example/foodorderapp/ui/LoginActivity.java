package com.example.foodorderapp.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static final String TAG = "LoginActivity";

    private TextView tvSignup;
    private TextView tvLoginError;
    private EditText edtEmail;
    private EditText edtPassword;
    private ProgressBar progressBar;

    private GoogleSignInClient googleSignInClient;
    private UserService userService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeViews();
        setupUserService();
        setupSignupText();
        setupClickListeners();
        setupGoogleSignIn();
    }

    private void initializeViews() {
        tvSignup = findViewById(R.id.tv_signup);
        tvLoginError = findViewById(R.id.tv_login_error);
        edtEmail = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
    }

    private void setupUserService() {
        userService = new UserService();
    }

    private void setupSignupText() {
        String text = "Nếu bạn chưa có tài khoản, vui lòng Đăng kí.";
        SpannableString spannableString = new SpannableString(text);

        int startIndex = text.indexOf("Đăng kí");
        int endIndex = startIndex + "Đăng kí".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                showProgress();
                navigateTo(RegisterActivity1.class);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.red));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.setText(spannableString);
        tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignup.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupClickListeners() {
        Button btnForget = findViewById(R.id.btn_forget);
        btnForget.setOnClickListener(v -> {
            showProgress();
            navigateTo(ForgetActivity.class);
        });

        Button btnLogin = findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(v -> handleEmailLogin());

        LinearLayout btnLoginGoogle = findViewById(R.id.btn_login_google);
        btnLoginGoogle.setOnClickListener(v -> handleGoogleSignIn());
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void handleEmailLogin() {
        showProgress();

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            showError("Vui lòng nhập đủ thông tin");
            hideProgress();
            return;
        }

        userService.loginUser(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulLogin();
                    } else {
                        showError("Sai tài khoản hoặc mật khẩu");
                        hideProgress();
                    }
                });
    }

    private void handleSuccessfulLogin() {
        userService.getUser()
                .addOnSuccessListener(documentSnapshot -> {
                    hideProgress();
                    navigateBasedOnUserProfile(documentSnapshot.exists() ?
                            documentSnapshot.toObject(UserModel.class) : null);
                })
                .addOnFailureListener(e -> {
                    hideProgress();
                    showError("Không thể lấy dữ liệu người dùng");
                });
    }

    private void handleGoogleSignIn() {
        showProgress();
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            processGoogleSignInResult(data);
        }
    }

    private void processGoogleSignInResult(Intent data) {
        try {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            GoogleSignInAccount account = task.getResult(ApiException.class);

            if (account != null) {
                authenticateWithFirebase(account.getIdToken());
            }
        } catch (ApiException e) {
            Log.w(TAG, "Google sign in failed", e);
            Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            hideProgress();
        }
    }

    private void authenticateWithFirebase(String idToken) {
        userService.loginWithGoogle(idToken)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        handleSuccessfulLogin();
                    } else {
                        Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                        hideProgress();
                    }
                });
    }

    private void navigateBasedOnUserProfile(UserModel userModel) {
        Class<?> targetActivity = (userModel == null || isIncompleteProfile(userModel)) ?
                RegisterActivity1.class : HomeActivity.class;
        navigateTo(targetActivity);
    }

    private boolean isIncompleteProfile(UserModel userModel) {
        return userModel.getFullName() == null || userModel.getFullName().isEmpty();
    }

    private void navigateTo(Class<?> cls) {
        startActivity(new Intent(LoginActivity.this, cls));
        finish();
    }

    private void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    private void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    private void showError(String message) {
        tvLoginError.setVisibility(View.VISIBLE);
        tvLoginError.setText(message);
    }
}