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
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

public class MainActivity extends AppCompatActivity {
    private TextView tvSignup;
    private ClickableSpan clickableSpan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        tvSignup = findViewById(R.id.tv_signup);
        String text = "Nếu bạn chưa có tài khoản, vui lòng Đăng kí.";
        SpannableString spannableString = new SpannableString(text);

        int start = text.indexOf("Đăng kí");
        int end = start + "Đăng kí".length();
// Đặt ClickableSpan cho từ "Đăng kí"
        clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                // Chuyển sang màn hình đăng ký
//                Intent intent = new Intent(IntroActivity.this, RegisterActivity.class);
//                startActivity(intent);
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(ContextCompat.getColor(MainActivity.this, R.color.red));
                ds.setUnderlineText(false);
            }
        };

        spannableString.setSpan(clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tvSignup.setText(spannableString);
        tvSignup.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignup.setHighlightColor(Color.TRANSPARENT); // Tắt màu nền khi click
    }
}
