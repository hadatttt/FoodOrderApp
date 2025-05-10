package com.example.foodorderapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.foodorderapp.model.ShopModel;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.ui.LoginActivity;

import java.util.Arrays;
import java.util.List;

public class IntroActivity extends AppCompatActivity {

    private ImageView[] dots;
    private int currentIndex = 0;
    private Button btnNext, btnCancel;
    private ImageView ivOrder;
    private TextView tv;
    private int[] imageResources;


    private EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //test

        //test
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        ShopService shopService = new ShopService();

        List<String> categories3 = Arrays.asList("Fast Food", "Burger");
        ShopModel shop3 = new ShopModel(
                3,
                "Pizza Hut",
                "789 Trần Phú, Đà Nẵng",
                12.0f,
                "https://example.com/pizzahut.jpg",
                "Mua 1 tặng 1 vào thứ 4 hàng tuần!",
                categories3
        );

        List<String> categories4 = Arrays.asList("Coffee", "Dessert");
        ShopModel shop4 = new ShopModel(
                4,
                "Highlands Coffee",
                "321 Bạch Đằng, Đà Nẵng",
                5.0f,
                "https://example.com/highlands.jpg",
                "Giảm 20% cho đơn hàng đầu tiên!",
                categories4
        );

        List<String> categories5 = Arrays.asList("Fried Chicken", "Fast Food");
        ShopModel shop5 = new ShopModel(
                5,
                "KFC",
                "555 Lê Lợi, Đà Nẵng",
                9.0f,
                "https://example.com/kfc.jpg",
                "Combo gà rán giá chỉ từ 49k!",
                categories5
        );

        List<String> categories6 = Arrays.asList("Noodles", "Asian");
        ShopModel shop6 = new ShopModel(
                6,
                "Phở 24",
                "888 Hùng Vương, Đà Nẵng",
                6.5f,
                "https://example.com/pho24.jpg",
                "Phở bò tái ngon tuyệt hảo!",
                categories6
        );

        List<String> categories7 = Arrays.asList("BBQ", "Korean");
        ShopModel shop7 = new ShopModel(
                7,
                "Gogi House",
                "777 Nguyễn Tri Phương, Đà Nẵng",
                18.0f,
                "https://example.com/gogi.jpg",
                "Thịt nướng không khói chuẩn Hàn Quốc!",
                categories7
        );

        List<String> categories8 = Arrays.asList("Seafood", "Vietnamese");
        ShopModel shop8 = new ShopModel(
                8,
                "Hải Sản Bé Mặn",
                "999 Võ Nguyên Giáp, Đà Nẵng",
                25.0f,
                "https://example.com/beman.jpg",
                "Hải sản tươi sống, giá cả phải chăng!",
                categories8
        );

        List<String> categories9 = Arrays.asList("Milk Tea", "Beverage");
        ShopModel shop9 = new ShopModel(
                9,
                "TocoToco",
                "222 Nguyễn Chí Thanh, Đà Nẵng",
                4.5f,
                "https://example.com/tocotoco.jpg",
                "Trà sữa trân châu hoàng kim!",
                categories9
        );

        List<String> categories10 = Arrays.asList("Vegetarian", "Healthy");
        ShopModel shop10 = new ShopModel(
                10,
                "Loving Hut",
                "444 Lê Thanh Nghị, Đà Nẵng",
                7.0f,
                "https://example.com/lovinghut.jpg",
                "Ăn chay thanh đạm, sống khỏe mạnh!",
                categories10
        );

        List<String> categories11 = Arrays.asList("Bakery", "Dessert");
        ShopModel shop11 = new ShopModel(
                11,
                "Tous les Jours",
                "101 Hoàng Diệu, Đà Nẵng",
                8.5f,
                "https://example.com/touslesjours.jpg",
                "Bánh ngọt Pháp chính hiệu!",
                categories11
        );

        List<String> categories12 = Arrays.asList("Ice Cream", "Dessert");
        ShopModel shop12 = new ShopModel(
                12,
                "Baskin Robbins",
                "202 Trưng Nữ Vương, Đà Nẵng",
                6.0f,
                "https://example.com/baskinrobbins.jpg",
                "Kem 31 vị ngon khó cưỡng!",
                categories12
        );

        shopService.addShop(shop3)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop3: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop3", e));

        shopService.addShop(shop4)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop4: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop4", e));

        shopService.addShop(shop5)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop5: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop5", e));

        shopService.addShop(shop6)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop6: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop6", e));

        shopService.addShop(shop7)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop7: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop7", e));

        shopService.addShop(shop8)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop8: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop8", e));

        shopService.addShop(shop9)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop9: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop9", e));

        shopService.addShop(shop10)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop10: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop10", e));

        shopService.addShop(shop11)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop11: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop11", e));

        shopService.addShop(shop12)
                .addOnSuccessListener(ref -> Log.d("AddShop", "Đã thêm shop12: " + ref.getId()))
                .addOnFailureListener(e -> Log.e("AddShop", "Lỗi thêm shop12", e));

        imageResources = new int[]{
                R.drawable.order,
                R.drawable.chef,
                R.drawable.delivery
        };

        tv = findViewById(R.id.textView);

        ivOrder = findViewById(R.id.iv_order);
        dots = new ImageView[] {
                findViewById(R.id.dot1),
                findViewById(R.id.dot2),
                findViewById(R.id.dot3)
        };
        updateDots();

        btnNext = findViewById(R.id.btn_next);
        btnNext.setOnClickListener(v -> {
            currentIndex = currentIndex + 1;
            if (currentIndex == 0) {
                tv.setText("Tất cả sở thích của bạn");
            } else if (currentIndex == 1) {
                tv.setText("Đặt món đến từ đầu bếp");
            } else if (currentIndex == 2) {
                tv.setText("Miễn phí vận chuyển");
            }
            if (currentIndex<3) {
                updateDots();
            } else {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
            ivOrder.setImageResource(imageResources[currentIndex]);
        });

        btnCancel = findViewById(R.id.btn_cancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void updateDots() {
        for (int i = 0; i < dots.length; i++) {
            dots[i].setSelected(i == currentIndex);
        }
    }
}
