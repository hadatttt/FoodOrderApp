package com.example.foodorderapp.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.FavouriteAdapter;
import com.example.foodorderapp.model.FavouriteModel;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.FavouriteService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class FavouriteActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private FavouriteAdapter favouriteAdapter;
    private List<FoodModel> favouriteList = new ArrayList<>();

    private FavouriteService favouriteService;
    private FoodService foodService;
    private String currentUserId ;
    private UserService userService;
    private ImageView btnRemove;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite);
        userService = new UserService();
        recyclerView = findViewById(R.id.rvFavourites);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ImageButton btnBack = findViewById(R.id.btnBack);
//        btnBack.setOnClickListener(v -> startActivity(new Intent(this, HomeActivity.class)));
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        favouriteService = new FavouriteService(); // Tùy bạn implement
        foodService = new FoodService(); // Tùy bạn implement
        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                String userId = task.getResult().getId();
                currentUserId = userId;
                Log.d("TestBug", currentUserId);
                loadFavouriteFoods();
            } else {
                Toast.makeText(this, "Không tìm thấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadFavouriteFoods() {
        favouriteService.getFavouriteList(currentUserId)
                .addOnSuccessListener(foodIds -> {
                    if (foodIds != null && !foodIds.isEmpty()) {
                        // Lấy chi tiết món ăn từ danh sách ID
                        final int total = foodIds.size();
                        final int[] loadedCount = {0};
                        for(int id : foodIds){
                            foodService.getFoodById(id).addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult() != null && !task.getResult().isEmpty()) {
                                    DocumentSnapshot doc = task.getResult().getDocuments().get(0);
                                    FoodModel food = doc.toObject(FoodModel.class);

                                    if (food != null) {
                                        favouriteList.add(food);

                                        Log.d("abc","size: " + favouriteList.size() + " ten:" + food.getName());
                                    }
                                } else {
                                    Exception e = task.getException();
                                    if (e != null) {
                                        Log.e("FoodDetailsError", "Lỗi khi tải chi tiết món ăn với id: " + id, e);
                                    } else {
                                        Log.e("FoodDetailsError", "Không tìm thấy món ăn với id: " + id);
                                    }
                                    Toast.makeText(this, "Không tìm thấy thông tin món ăn", Toast.LENGTH_SHORT).show();
                                }
                                loadedCount[0]++;
                                Log.d("abc", String.valueOf(loadedCount[0]));
                                Log.d("abc","size: " + favouriteList.size());
                                if (loadedCount[0] == total) {
                                    // Tất cả món ăn đã load xong mới cập nhật adapter
                                    if (!favouriteList.isEmpty()) {
                                        favouriteAdapter = new FavouriteAdapter(this, favouriteList);
                                        recyclerView.setAdapter(favouriteAdapter);
                                        favouriteAdapter.setOnItemClickListener(new FavouriteAdapter.OnItemClickListener() {
                                            @Override
                                            public void onRemoveClick(FoodModel food) {
                                                removeFromFavourite(food);
                                            }
                                        });
                                        favouriteAdapter.notifyDataSetChanged();
                                    } else {
                                        Toast.makeText(this, "Chưa có món yêu thích nào", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }

                    } else {
                        Toast.makeText(this, "Chưa có món ", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi tải dữ liệu yêu thích", Toast.LENGTH_SHORT).show();
                });

    }

    private void removeFromFavourite(FoodModel food) {
        favouriteService.removeFoodFromFavourite(currentUserId, food.getFoodId())
                .addOnSuccessListener(unused -> {
                    Toast.makeText(this, "Đã xoá khỏi yêu thích: " + food.getName(), Toast.LENGTH_SHORT).show();
                    recreate();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Không thể xoá", Toast.LENGTH_SHORT).show();
                });
    }
}
