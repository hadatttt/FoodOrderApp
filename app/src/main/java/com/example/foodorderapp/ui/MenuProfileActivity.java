package com.example.foodorderapp.ui;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

import java.io.File;

public class MenuProfileActivity extends AppCompatActivity {
    private UserService userService;
    private ImageView imgAvatar;
    private TextView profileName;
    private ImageButton btnBack;
    private LinearLayout btnOrders;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_menu_profile);
        userService = new UserService();
        imgAvatar = findViewById(R.id.profileImage);
        profileName = findViewById(R.id.profileName);
        btnOrders = findViewById(R.id.btnOrders);
        displayUserDetail();
        Button btnLogout = findViewById(R.id.btnLogout);
        LinearLayout btnInfo = findViewById(R.id.btnInfo);
        LinearLayout btnAccount = findViewById(R.id.btnAccount);
        LinearLayout btnOrders = findViewById(R.id.btnOrders);
        LinearLayout btnFavourite = findViewById(R.id.btnFavourite);
        LinearLayout btnPayment = findViewById(R.id.btnPayment);
        LinearLayout btnFAQs = findViewById(R.id.btnFAQs);
        LinearLayout btnSetting = findViewById(R.id.btnSetting);
        btnBack = findViewById(R.id.btnBack);
        btnAccount.setOnClickListener(v -> openAccount());
        btnOrders.setOnClickListener(v -> openOrders());
        btnFavourite.setOnClickListener(v -> openFavourite());
        btnPayment.setOnClickListener(v -> openPayment());
        btnFAQs.setOnClickListener(v -> openFAQs());
        btnSetting.setOnClickListener(v -> openSetting());
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfileActivity.this, InfoActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });
        ActivityResultLauncher<Intent> infoLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String avatarUri = data.getStringExtra("avatarUri");
                            if (avatarUri != null && !avatarUri.isEmpty()) {
                                imgAvatar.setImageURI(Uri.parse(avatarUri));
                            }
                        }
                    }
                });
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận")
                    .setMessage("Bạn có chắc chắn muốn đăng xuất?")
                    .setPositiveButton("Đăng xuất", (dialog, which) -> {
                        FirebaseAuth.getInstance().signOut();

                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("Hủy", null)
                    .show();
        });


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        btnOrders.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MenuProfileActivity.this, OrderActivity.class);
                startActivity(intent);
            }
        });
    }

    private void openSetting() {
        // xử lý khi bấm vào
    }

    private void openFAQs() {
        // xử lý khi bấm vào
    }

    private void openPayment() {
        // xử lý khi bấm vào
    }

    private void openFavourite() {
        // xử lý khi bấm vào
    }

    private void openOrders() {
        // xử lý khi bấm vào
    }

    private void openAccount() {
        Intent intent = new Intent(MenuProfileActivity.this, AccountActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void updateAvatar(){
        File file = new File(getFilesDir(), "avatarIMG.jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imgAvatar.setImageBitmap(bitmap);
    }
    private void displayUserDetail() {
        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                String name = doc.getString("fullName");
                profileName.setText(name);
                updateAvatar();
            } else {
                Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            String uriStr = data.getStringExtra("avatarUri");
            if (uriStr != null) {
                imgAvatar.setImageURI(Uri.parse(uriStr));
            }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        displayUserDetail();
    }

}