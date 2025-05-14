package com.example.foodorderapp.ui;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.foodorderapp.R;
import com.example.foodorderapp.databinding.ActivityHome1Binding;
import com.example.foodorderapp.ui.fragment.HomeFragment;
import com.example.foodorderapp.ui.fragment.OrderFragment;
import com.example.foodorderapp.ui.fragment.ProfileFragment;

public class HomeActivity1 extends AppCompatActivity {

    private ActivityHome1Binding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityHome1Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        loadMainFragment();
        setUpComponents();
    }

    private void setUpComponents() {
        binding.bottomNavigation.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if (itemId == R.id.navbar_home) {
                loadMainFragment();
                return true;
            } else if (itemId == R.id.navbar_order) {
                OrderFragment orderFragment = new OrderFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_main_fragment_container, orderFragment);
                transaction.commit();
                return true;
            } else if (itemId == R.id.navbar_user) {
                ProfileFragment profileFragment = new ProfileFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.home_main_fragment_container, profileFragment);
                transaction.commit();
                return true;
            }
            return false;
        });
    }

    private void loadMainFragment() {
        HomeFragment mainFragment = new HomeFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.home_main_fragment_container, mainFragment);
        transaction.commit();
    }
}