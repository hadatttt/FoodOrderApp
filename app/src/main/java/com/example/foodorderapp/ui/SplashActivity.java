package com.example.foodorderapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.example.foodorderapp.IntroActivity;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashActivity extends AppCompatActivity {
    private boolean loadingSplash = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpSplashScreen();
    }

//    private void setUpSplashScreen() {
//        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
//        splashScreen.setKeepOnScreenCondition(() -> loadingSplash);
//
//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            loadingSplash = false;
//            FirebaseApp.initializeApp(this);
//
//            Intent intent = new Intent(this, IntroActivity.class);
//            startActivity(intent);
//            finish();
//        }, 2000);
//
//        splashScreen.setOnExitAnimationListener(splashScreenView -> {
//            splashScreenView.getView().setAlpha(1f);
//            splashScreenView.getView().animate()
//                    .alpha(0f)
//                    .setDuration(300)
//                    .withEndAction(splashScreenView::remove)
//                    .start();
//        });
//    }

    private void setUpSplashScreen() {
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> loadingSplash);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            loadingSplash = false;
            FirebaseApp.initializeApp(this);
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                Intent intent = new Intent(this, IntroActivity.class);
                startActivity(intent);
                finish();
            } else {
                // User is logged in, proceed to main screen
                Intent intent = new Intent(this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
            finish();
        }, 2000);

        splashScreen.setOnExitAnimationListener(splashScreenView -> {
            splashScreenView.getView().setAlpha(1f);
            splashScreenView.getView().animate()
                    .alpha(0f)
                    .setDuration(300)
                    .withEndAction(splashScreenView::remove)
                    .start();
        });
    }


}