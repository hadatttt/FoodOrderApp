package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.foodorderapp.R;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;

public class DetailFoodActivity extends AppCompatActivity {
    private FoodService foodService = new FoodService();
    private ShopService shopService = new ShopService();
    private UserService userService = new UserService();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_food); // Cần đúng tên file layout XML của bạn

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        int foodId = getIntent().getIntExtra("foodid", -1);
        // TODO: Viết logic xử lý ở đây nếu cần
        foodService.getFoodDetails(foodId).addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                DocumentSnapshot document = task.getResult().getDocuments().get(0);
                FoodModel currentFood = document.toObject(FoodModel.class);

                if (currentFood != null) {
                    // Lấy storeId
                    Long storeIdLong = document.getLong("storeId");
                    int storeId = storeIdLong != null ? storeIdLong.intValue() : -1;

                    // Lấy rating
                    Double ratingDouble = document.getDouble("rating");
                    float rating = ratingDouble != null ? ratingDouble.floatValue() : 0.1f;

                    // Lấy name
                    String name = document.getString("name");

                    // Lấy price
                    Double priceDouble = document.getDouble("price");
                    double price = priceDouble != null ? priceDouble : 0.3;
                    TextView foodName = findViewById(R.id.tvFoodName);
                    TextView ratting = findViewById(R.id.tvRate);
                    foodName.setText(name);
                    ratting.setText((float) rating);

                    Log.d("DetailFood", "storeId: " + storeId);
                    Log.d("DetailFood", "name: " + name);
                    Log.d("DetailFood", "rating: " + rating);
                    Log.d("DetailFood", "price: " + price);

                    // Gọi shopService để lấy thông tin cửa hàng
                    shopService.getShopById(storeId).addOnCompleteListener(shopTask -> {
                        if (shopTask.isSuccessful() && shopTask.getResult() != null && !shopTask.getResult().isEmpty()) {
                            DocumentSnapshot shopDoc = shopTask.getResult().getDocuments().get(0);

                            String shopName = shopDoc.getString("name");
                            String caption = shopDoc.getString("caption");
                            String address = shopDoc.getString("address");

                            Log.d("DetailShop", "Shop Name: " + shopName);
                            Log.d("DetailShop", "Caption: " + caption);
                            Log.d("DetailShop", "Address: " + address);

                            TextView shopn = findViewById(R.id.tvShopName);
                            TextView ad = findViewById(R.id.tvDesc);
                            shopn.setText(shopName);
                            ad.setText(caption);


                        } else {
                            Toast.makeText(this, "Không tìm thấy cửa hàng", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
            }
        });



    }
}
