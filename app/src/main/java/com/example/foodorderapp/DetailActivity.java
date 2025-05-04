package com.example.foodorderapp;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.databinding.ActivityDetailBinding;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.text.DecimalFormat;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



public class DetailActivity extends AppCompatActivity {
    private ActivityDetailBinding binding;
    public int quantity = 1;
    public int basePrice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        basePrice = 200000;
        binding.btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity++;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updatePrice();
            }
        });
        binding.btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
                updatePrice();
            }
        });
        FoodApiService apiService = ApiClient.getClient().create(FoodApiService.class);
        Call<List<FoodModel>> call = apiService.getAllFoods();

        call.enqueue(new Callback<List<FoodModel>>() {
            @Override
            public void onResponse(Call<List<FoodModel>> call, Response<List<FoodModel>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<FoodModel> foodList = response.body();
                    for (FoodModel food : foodList) {
                        Log.d(TAG, "Food: " + food.getName() + ", Price: " + food.getPrice());
                        binding.tvFoodName.setText(food.getName());
                        binding.tvDesc.setText(food.getDesc());
                        binding.tvStore.setText(food.getStoreId());
                        //binding.tvRate.setText(food.getRating());
                        binding.tvPrice.setText(food.getPrice());
                    }
                } else {
                    Log.e(TAG, "API Response Error");
                }
            }

            @Override
            public void onFailure(Call<List<FoodModel>> call, Throwable t) {
                Log.e(TAG, "API Call Failure: " + t.getMessage());
            }
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void updatePrice(){
        int totalPrice = quantity * basePrice;
        DecimalFormat formatter = new DecimalFormat("#,###", DecimalFormatSymbols.getInstance(Locale.getDefault()));
        String formattedPrice = formatter.format(totalPrice);
        binding.tvPrice.setText(formattedPrice + "đ");
    }
}