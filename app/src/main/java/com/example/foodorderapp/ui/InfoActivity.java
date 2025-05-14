package com.example.foodorderapp.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.provider.OpenableColumns;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class InfoActivity extends AppCompatActivity {
    private EditText editFullName;
    private EditText editAddress;
    private EditText editEmail;
    private EditText editPhone;
    private Button btnSave;
    private ImageView imgAvatar;
    private UserService userService;
    private Uri selectedImageUri;
    private String imgUrl;
    private ImageButton btnBack;
    private static final int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_info);
        init();
        imgAvatar.setOnClickListener(v -> openImageChooser());
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateInfo();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("avatarUri", selectedImageUri.toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    public void init(){
        btnBack = findViewById(R.id.btnBack);
        editFullName = findViewById(R.id.editFullName);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSave);
        imgAvatar = findViewById(R.id.imgAvatar);
        userService = new UserService();
        displayUserDetail();
    }
    private void displayUserDetail() {
        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                String fullName = doc.getString("fullName");
                String address = doc.getString("address");
                String phone = doc.getString("phone");
                // Hiển thị lên giao diện
                editFullName.setText(fullName);
                editAddress.setText(address);
                editPhone.setText(phone);
                updateAvatar();
            } else {
                Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void updateInfo(){
                UserModel user = new UserModel(
                        editFullName.getText().toString().trim(),
                        editPhone.getText().toString().trim(),
                        editAddress.getText().toString().trim()
                );
                userService.updateUser(user);
    }
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                saveImageToInternalStorage(selectedImageUri);
            }
        }
    }
    public void updateAvatar(){
        File file = new File(getFilesDir(), "avatarIMG.jpg");
        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
        imgAvatar.setImageBitmap(bitmap);
    }
    private void saveImageToInternalStorage(Uri imageUri) {
        try {
            // Đọc ảnh từ Uri
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            // Tạo tên file
            String fileName = "avatarIMG.jpg";

            // Ghi ảnh vào bộ nhớ trong của app (private)
            FileOutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();
            updateAvatar();
            Toast.makeText(this, "Đã lưu ảnh vào bộ nhớ trong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }



}