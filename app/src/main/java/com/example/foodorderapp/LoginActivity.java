package com.example.foodorderapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginActivity extends AppCompatActivity {
    private TextView tvSignup;
    private ClickableSpan clickableSpan;
    private Button btnForget, btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvSignup = findViewById(R.id.tv_signup);
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
//                Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
//                startActivity(intent);
            }
        });
    }
}