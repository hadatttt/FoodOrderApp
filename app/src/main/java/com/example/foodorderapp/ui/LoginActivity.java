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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private TextView tvSignup, tvLoginError;
    private Button btnForget, btnLogin;
    private EditText edtUsername, edtPassword;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    public UserService userService = new UserService();
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignup = findViewById(R.id.tv_signup);
        tvLoginError = findViewById(R.id.tv_login_error);
        btnForget = findViewById(R.id.btn_forget);
        btnLogin = findViewById(R.id.btn_login);
        edtUsername = findViewById(R.id.edt_email);
        edtPassword = findViewById(R.id.edt_password);

        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        mAuth = FirebaseAuth.getInstance();

        // Tạo đoạn văn bản có phần "Đăng kí" có thể click
        String text = "Nếu bạn chưa có tài khoản, vui lòng Đăng kí.";
        SpannableString spannableString = new SpannableString(text);
        int start = text.indexOf("Đăng kí");
        int end = start + "Đăng kí".length();
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {

                progressBar.setVisibility(View.VISIBLE);
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
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

        // Quên mật khẩu
        btnForget.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
            startActivity(intent);
            finish();
        });

        // Đăng nhập bằng email & mật khẩu
        btnLogin.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            String inputEmail = edtUsername.getText().toString().trim();
            String inputPassword = edtPassword.getText().toString().trim();
            if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                tvLoginError.setVisibility(View.VISIBLE);
                tvLoginError.setText("Vui lòng nhập đủ thông tin");
                return;
            }

            userService.loginUser(inputEmail, inputPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
//                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            Intent intent = new Intent(LoginActivity.this, SearchActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            tvLoginError.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.GONE);
                            tvLoginError.setText("Sai tài khoản hoặc mật khẩu");
                        }
                    });
        });

        // Cấu hình đăng nhập bằng Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Sự kiện click nút Google
        LinearLayout googleLogin = findViewById(R.id.btn_login_google);
        googleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            progressBar.setVisibility(View.VISIBLE);
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    // Nhận kết quả từ Google Sign-in
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();

                // Đăng nhập vào Firebase thông qua UserService
                userService.loginWithGoogle(idToken)
                        .addOnCompleteListener(this, task1 -> {
                            if (task1.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (ApiException e) {
                progressBar.setVisibility(View.GONE);
                Log.w("GoogleSignIn", "Google sign in failed", e);
                Toast.makeText(this, "Đăng nhập Google thất bại", Toast.LENGTH_SHORT).show();
            }
        }
    }
}