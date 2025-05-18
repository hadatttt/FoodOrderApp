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
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.service.FCMTokenService;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.websocket.WebSocketManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextView tvSignup, tvLoginError;
    private Button btnForget, btnLogin;
    private EditText edtUsername, edtPassword;
    private ProgressBar progressBar;
    private FrameLayout loadingOverlay;

    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private final UserService userService = new UserService();
    private FirebaseUser user;

    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        WebSocketManager.init(getApplicationContext());

        initEdgeToEdge();
        initViews();
        setupClickableSignupText();
        setupButtons();
        setupGoogleSignIn();
    }

    private void initEdgeToEdge() {
        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void initViews() {
        tvSignup = findViewById(R.id.tv_signup);
        tvLoginError = findViewById(R.id.tv_login_error);
        btnForget = findViewById(R.id.btn_forget);
        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);
        progressBar = findViewById(R.id.progressBar);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        mAuth = FirebaseAuth.getInstance();

        hideLoading();
        tvLoginError.setVisibility(View.GONE);
    }

    private void setupClickableSignupText() {
        String text = "Chưa có tài khoản, vui lòng Đăng kí.";
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf("Đăng kí");
        int end = start + "Đăng kí".length();

        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                startActivityWithLoading(RegisterActivity.class);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(LoginActivity.this, R.color.red));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.setText(spannableString);
        tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignup.setHighlightColor(Color.TRANSPARENT);
    }

    private void setupButtons() {
        btnForget.setOnClickListener(v -> startActivityWithLoading(ForgetActivity.class));

        btnLogin.setOnClickListener(v -> {
            tvLoginError.setVisibility(View.GONE);

            String email = edtUsername.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                showError("Vui lòng nhập đủ thông tin");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showError("Email không đúng định dạng");
                return;
            }

            Log.d("LoginActivity", "Email nhập vào: " + email);

            showLoading();
            userService.loginUser(email, password)
                    .addOnCompleteListener(task -> {
                        hideLoading(); // Đảm bảo ẩn loading bất kể thành công hay thất bại

                        if (task.isSuccessful()) {
                            onLoginSuccess();
                        } else {
                            Exception e = task.getException();
                            Log.e("LoginActivity", "Login failed", e);
                            showError("Sai tài khoản hoặc mật khẩu"); // Luôn dùng câu này
                        }
                    });

        });

    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleLoginBtn = findViewById(R.id.btn_login_google);
        googleLoginBtn.setOnClickListener(v -> {
            showLoading();
            startActivityForResult(mGoogleSignInClient.getSignInIntent(), RC_SIGN_IN);
        });
    }

    private void onLoginSuccess() {
        FCMTokenService.sendTokenToFirestore();

        user = mAuth.getCurrentUser();
        if (user == null) {
            hideLoading();
            showError("Không lấy được thông tin người dùng");
            return;
        }

        FirebaseFirestore.getInstance()
                .collection("users")
                .document(user.getUid())
                .get()
                .addOnSuccessListener(doc -> {
                    hideLoading();
                    if (doc.exists()) {
                        Long storeIdLong = doc.getLong("shopid");
                        String storeId = (storeIdLong != null) ? String.valueOf(storeIdLong) : null;
                        redirectAfterLogin(storeId);
                    } else {
                        showError("Không tìm thấy dữ liệu user");
                    }
                })
                .addOnFailureListener(e -> {
                    hideLoading();
                    showError("Lỗi truy vấn dữ liệu");
                    Log.e("Firestore", "Lỗi: ", e);
                });
    }

    private void redirectAfterLogin(String storeId) {
        Intent intent;
        if (storeId != null && !storeId.isEmpty()) {
            Log.d("LoginActivity", "Chuyển sang ShopManager với shopid = " + storeId);
            try {
                int storeIdInt = Integer.parseInt(storeId);
                WebSocketManager.getInstance().registerStore(storeIdInt);
            } catch (NumberFormatException e) {
                Log.e("LoginActivity", "Lỗi chuyển đổi storeId sang int", e);
            }

            intent = new Intent(this, ShopManager.class);
            intent.putExtra("shopid", storeId);
        } else {
            Log.d("LoginActivity", "Chuyển sang HomeActivity, không có shopid");
            WebSocketManager.getInstance().registerUser(user.getUid());
            intent = new Intent(this, HomeActivity.class);

        }
        startActivity(intent);
        finish();
    }


    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.VISIBLE);
        btnLogin.setEnabled(false);
        btnForget.setEnabled(false);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        btnLogin.setEnabled(true);
        btnForget.setEnabled(true);
    }

    private void showError(String message) {
        tvLoginError.setVisibility(View.VISIBLE);
        tvLoginError.setText(message);
    }

    private void startActivityWithLoading(Class<?> clazz) {
        showLoading();
        startActivity(new Intent(LoginActivity.this, clazz));
        hideLoading();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account == null) ;
                String idToken = account.getIdToken();

                showLoading();
                userService.loginWithGoogle(idToken)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                onLoginSuccess();
                            } else {
                                hideLoading();
                                Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });

            } catch (ApiException e) {
                hideLoading();
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}