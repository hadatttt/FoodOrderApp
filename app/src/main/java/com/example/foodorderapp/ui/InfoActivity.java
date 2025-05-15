package com.example.foodorderapp.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;
import android.provider.OpenableColumns;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.api.ApiClient;
import com.example.foodorderapp.api.ApiService;
import com.example.foodorderapp.api.ProvinceDetailResponse;
import com.example.foodorderapp.model.DistrictModel;
import com.example.foodorderapp.model.ProvinceModel;
import com.example.foodorderapp.model.UserModel;
import com.example.foodorderapp.model.WardModel;
import com.example.foodorderapp.service.MapService;
import com.example.foodorderapp.service.UserService;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.type.LatLng;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends AppCompatActivity implements OnMapReadyCallback {
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
    private FrameLayout loadingOverlay;
    private ImageView iconSaved;
    private static final int PICK_IMAGE_REQUEST = 1;
    private ProgressBar progressBar;
    private GoogleMap mMap;
    private SupportMapFragment mapFragment;
    private MapService mapService;
    private double currentLat = 0, currentLng = 0;

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
                String uriString;

                if (selectedImageUri != null) {
                    uriString = selectedImageUri.toString();
                } else {
                    // Lấy avatar hiện tại từ ImageView (nếu đã hiển thị từ đầu)
                    Drawable drawable = imgAvatar.getDrawable();
                    if (drawable instanceof BitmapDrawable) {
                        Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                        Uri tempUri = getImageUri(getApplicationContext(), bitmap);
                        uriString = tempUri.toString();
                    } else {
                        uriString = ""; // Trường hợp không có ảnh
                    }
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra("avatarUri", uriString);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        });

        editAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddressDialog();
            }
        });

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private Uri getImageUri(Context context, Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "Avatar", null);
        return Uri.parse(path);
    }

    public void init(){
        btnBack = findViewById(R.id.btnBack);
        editFullName = findViewById(R.id.editFullName);
        editAddress = findViewById(R.id.editAddress);
        editPhone = findViewById(R.id.editPhone);
        btnSave = findViewById(R.id.btnSave);
        editEmail = findViewById(R.id.editEmail);
        imgAvatar = findViewById(R.id.imgAvatar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        iconSaved = findViewById(R.id.iconSaved);
        progressBar = findViewById(R.id.progressBar);
        userService = new UserService();
        mapService = new MapService();
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        displayUserDetail();
    }
    private void displayUserDetail() {
        userService.getUser().addOnCompleteListener(task -> {
            if (task.isSuccessful() && task.getResult() != null) {
                DocumentSnapshot doc = task.getResult();
                String fullName = doc.getString("fullName");
                String address = doc.getString("address");
                String phone = doc.getString("phone");

                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if (firebaseUser != null) {
                    String email = firebaseUser.getEmail();
                    editEmail.setText(email);
                }
                // Hiển thị lên giao diện
                editFullName.setText(fullName);
                editAddress.setText(address);
                editPhone.setText(phone);
                updateAvatar();
                if (address != null && !address.trim().isEmpty()) {
                    loadMapWithAddress(address);
                }
            } else {
                Toast.makeText(this, "Không thể lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadMapWithAddress(String address) {
        if (mapFragment == null || mMap == null) {
            Log.e("InfoActivity", "MapFragment hoặc GoogleMap chưa khởi tạo");
            return;
        }
        if (address == null || address.trim().isEmpty()) {
            Toast.makeText(this, "Địa chỉ trống, không thể hiển thị bản đồ", Toast.LENGTH_SHORT).show();
            if (mapFragment.getView() != null) {
                mapFragment.getView().setVisibility(View.GONE);
            }
            return;
        }

        Log.d("InfoActivity", "Tìm tọa độ cho địa chỉ: " + address);
        mapService.getCoordinatesFromAddress(address, (lat, lng) -> {

            Log.d("InfoActivity", "Received coordinates: lat=" + lat + ", lng=" + lng);

            runOnUiThread(() -> {
                if (lat != 0 && lng != 0) {
                    currentLat = lat;
                    currentLng = lng;
                    mMap.clear();
                    com.google.android.gms.maps.model.LatLng pos = new com.google.android.gms.maps.model.LatLng(lat, lng);
                    mMap.addMarker(new MarkerOptions().position(pos).title("Địa chỉ của bạn"));
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15));
                    if (mapFragment.getView() != null) {
                        mapFragment.getView().setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(this, "Không tìm thấy vị trí cho địa chỉ: " + address, Toast.LENGTH_LONG).show();
                    Log.e("InfoActivity", "Không tìm thấy vị trí cho địa chỉ: " + address);
                    if (mapFragment.getView() != null) {
                        mapFragment.getView().setVisibility(View.GONE);
                    }
                }
            });
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("InfoActivity", "GoogleMap đã sẵn sàng");
        String address = editAddress.getText().toString();
        if (address != null && !address.trim().isEmpty()) {
            loadMapWithAddress(address);
        }
    }


    public void updateInfo(){
        loadingOverlay.setVisibility(View.VISIBLE);
        UserModel user = new UserModel(
            editFullName.getText().toString().trim(),
            editPhone.getText().toString().trim(),
            editAddress.getText().toString().trim()
        );
        userService.updateUser(user);
        FirebaseUser userFire = FirebaseAuth.getInstance().getCurrentUser();
        if (userFire != null) {
            userFire.updateEmail(editEmail.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            showSavedCheckmark();
//                            Toast.makeText(this, "Đã cập nhật email", Toast.LENGTH_SHORT).show();
                        } else {
                            loadingOverlay.setVisibility(View.GONE);
//                            Toast.makeText(this, "Không thể cập nhật email. Hãy đăng nhập lại.", Toast.LENGTH_LONG).show();
                        }
                    });
        }

    }
    private void openImageChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Chọn ảnh đại diện"), PICK_IMAGE_REQUEST);
    }
    private void showSavedCheckmark() {
        loadingOverlay.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        iconSaved.setVisibility(View.VISIBLE);
        new Handler().postDelayed(() -> {
            iconSaved.setVisibility(View.GONE);
            loadingOverlay.setVisibility(View.GONE);  // di chuyển dòng này vào đây
        }, 2000);
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
//            Toast.makeText(this, "Đã lưu ảnh vào bộ nhớ trong", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
//            Toast.makeText(this, "Lỗi khi lưu ảnh", Toast.LENGTH_SHORT).show();
        }
    }
    private void showAddressDialog() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_address, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        AlertDialog dialog = builder.create();

        Spinner spinnerProvince = dialogView.findViewById(R.id.spinner_province);
        Spinner spinnerDistrict = dialogView.findViewById(R.id.spinner_district);
        Spinner spinnerWard = dialogView.findViewById(R.id.spinner_ward);
        EditText editDetail = dialogView.findViewById(R.id.edit_detail_address);
        Button btnConfirm = dialogView.findViewById(R.id.btn_confirm_address);

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Load provinces
        apiService.getProvinces().enqueue(new Callback<List<ProvinceModel>>() {
            @Override
            public void onResponse(Call<List<ProvinceModel>> call, Response<List<ProvinceModel>> response) {
                if (response.isSuccessful()) {
                    List<ProvinceModel> provinces = response.body();
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(InfoActivity.this, android.R.layout.simple_spinner_item);
                    provinceAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    for (ProvinceModel p : provinces) {
                        provinceAdapter.add(p.getName());
                    }

                    spinnerProvince.setAdapter(provinceAdapter);

                    spinnerProvince.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int provinceCode = provinces.get(position).getCode();

                            apiService.getDistricts(provinceCode).enqueue(new Callback<ProvinceDetailResponse>() {
                                @Override
                                public void onResponse(Call<ProvinceDetailResponse> call, Response<ProvinceDetailResponse> response) {
                                    if (response.isSuccessful()) {
                                        List<DistrictModel> districts = response.body().getDistricts();
                                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(InfoActivity.this, android.R.layout.simple_spinner_item);
                                        districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                        for (DistrictModel d : districts) {
                                            districtAdapter.add(d.getName());
                                        }

                                        spinnerDistrict.setAdapter(districtAdapter);

                                        spinnerDistrict.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                            @Override
                                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                                int districtCode = districts.get(pos).getCode();

                                                apiService.getWards(districtCode).enqueue(new Callback<DistrictModel>() {
                                                    @Override
                                                    public void onResponse(Call<DistrictModel> call, Response<DistrictModel> response) {
                                                        if (response.isSuccessful()) {
                                                            List<WardModel> wards = response.body().getWards();
                                                            ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(InfoActivity.this, android.R.layout.simple_spinner_item);
                                                            wardAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                                                            for (WardModel w : wards) {
                                                                wardAdapter.add(w.getName());
                                                            }
                                                            spinnerWard.setAdapter(wardAdapter);
                                                        }
                                                    }

                                                    @Override
                                                    public void onFailure(Call<DistrictModel> call, Throwable t) {
                                                        Log.e("WardError", t.getMessage());
                                                    }
                                                });
                                            }

                                            @Override
                                            public void onNothingSelected(AdapterView<?> parent) {}
                                        });
                                    }
                                }

                                @Override
                                public void onFailure(Call<ProvinceDetailResponse> call, Throwable t) {
                                    Log.e("DistrictError", t.getMessage());
                                }
                            });
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {}
                    });
                }
            }

            @Override
            public void onFailure(Call<List<ProvinceModel>> call, Throwable t) {
                Log.e("ProvinceError", t.getMessage());
            }
        });

        btnConfirm.setOnClickListener(v -> {
            String province = spinnerProvince.getSelectedItem().toString();
            String district = spinnerDistrict.getSelectedItem().toString();
            String ward = spinnerWard.getSelectedItem().toString();
            String detail = editDetail.getText().toString();
            String fullAddress = "";
            if (!detail.isEmpty()) {
                fullAddress = detail + ", " + ward + ", " + district + ", " + province;
            } else fullAddress = ward + ", " + district + ", " + province;
            editAddress.setText(fullAddress);
            dialog.dismiss();
            if (!fullAddress.trim().isEmpty()) {
                loadMapWithAddress(fullAddress);
            } else {
//                Toast.makeText(this, "Địa chỉ trống, không thể hiển thị bản đồ", Toast.LENGTH_SHORT).show();
                if (mapFragment != null && mapFragment.getView() != null) {
                    mapFragment.getView().setVisibility(View.GONE);
                }
            }
        });
        dialog.show();
    }


}