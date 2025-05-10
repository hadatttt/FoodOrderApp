package com.example.foodorderapp;

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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private TextView tvSignup, tvLoginError;
    private ClickableSpan clickableSpan;
    private Button btnForget, btnLogin;
    private FirebaseAuth mAuth;

    private EditText edtUsername, edtPassword;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        tvSignup = findViewById(R.id.tv_signup);
        mAuth = FirebaseAuth.getInstance();
        String text = "Nếu bạn chưa có tài khoản, vui lòng Đăng kí.";
        SpannableString spannableString = new SpannableString(text);

        btnForget = findViewById(R.id.btn_forget);
        btnLogin = findViewById(R.id.btn_login);

        int start = text.indexOf("Đăng kí");
        int end = start + "Đăng kí".length();
        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
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

        tvLoginError = findViewById(R.id.tv_login_error);

        btnForget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtUsername = findViewById(R.id.edt_email);
                edtPassword = findViewById(R.id.edt_password);
                String inputEmail = edtUsername.getText().toString().trim();
                String inputPassword = edtPassword.getText().toString().trim();
                if (inputEmail.isEmpty() || inputPassword.isEmpty()) {
                    tvLoginError.setVisibility(View.VISIBLE);
                    tvLoginError.setText("Vui lòng nhập đủ thông tin");
                    return;
                }

                mAuth.signInWithEmailAndPassword(inputEmail, inputPassword)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                intent.putExtra("user_email", inputEmail);
                                startActivity(intent);
                                finish();
                            } else {
                                tvLoginError.setVisibility(View.VISIBLE);
                                tvLoginError.setText("Sai tài khoản hoặc mật khẩu");
                            }
                        });
            }
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id)) // Lấy từ Firebase > Project settings
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        LinearLayout googleLogin = findViewById(R.id.btn_login_google);
        googleLogin.setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w("GoogleSignIn", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        String email = mAuth.getCurrentUser().getEmail();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        db.collection("users")
                                .whereEqualTo("email", email)
                                .get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    if (queryDocumentSnapshots.isEmpty()) {
                                        // Email chưa tồn tại -> thêm mới
                                        Map<String, Object> user = new HashMap<>();
                                        String username = email.split("@")[0];
                                        user.put("fullName", username);
                                        user.put("email", email);
                                        user.put("phone", "");
                                        user.put("address", "");

                                        db.collection("users")
                                                .add(user)
                                                .addOnSuccessListener(documentReference -> {
                                                    Log.d("Firestore", "Thêm người dùng mới thành công");
                                                })
                                                .addOnFailureListener(e -> {
                                                    Log.w("Firestore", "Lỗi khi thêm người dùng mới", e);
                                                });
                                    }
                                    // Dù thêm mới hay không, vẫn vào Home
                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                    intent.putExtra("user_email", email);
                                    startActivity(intent);
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(this, "Lỗi kiểm tra người dùng", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(this, "Google đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}