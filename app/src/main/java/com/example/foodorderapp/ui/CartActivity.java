package com.example.foodorderapp.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.foodorderapp.R;
import com.example.foodorderapp.adapter.CartAdapter;
import com.example.foodorderapp.api.ApiClient;
import com.example.foodorderapp.api.ApiService;
import com.example.foodorderapp.api.ProvinceDetailResponse;
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.DistrictModel;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.model.ProvinceModel;
import com.example.foodorderapp.model.WardModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.OrderService;
import com.example.foodorderapp.service.UserService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CartAdapter cartAdapter;
    private List<CartModel> cartList;
    private FirebaseFirestore db;
    private TextView tvPrice, tvAddress;
    private ImageButton btnBack;
    private Button btnPay, btnAddress;
    private ProgressBar progressBar;
    private FrameLayout loadingOverlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        EdgeToEdge.enable(this);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        loadingOverlay = findViewById(R.id.loadingOverlay);
        loadingOverlay.setVisibility(View.GONE);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        tvPrice = findViewById(R.id.tvPrice);
        tvAddress = findViewById(R.id.tv_address);

        UserService userService = new UserService();

        userService.getUser().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String address = documentSnapshot.getString("address");
                if (address != null && !address.isEmpty()) {
                    tvAddress.setText(address);
                } else {
                    tvAddress.setText("");
                }
            } else {
                tvAddress.setText("");
            }
        }).addOnFailureListener(e -> {
            tvAddress.setText("");
            e.printStackTrace();
        });
        recyclerView = findViewById(R.id.rv_cart);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        btnBack = findViewById(R.id.btn_back);
        btnPay = findViewById(R.id.btn_pay);
        btnAddress = findViewById(R.id.btnBreakdown);

        db = FirebaseFirestore.getInstance();
        cartList = new ArrayList<>();
        cartAdapter = new CartAdapter(this, cartList);
        recyclerView.setAdapter(cartAdapter);


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CartActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserService userService = new UserService();
                userService.getUser().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String address = documentSnapshot.getString("address");
                        String phone = documentSnapshot.getString("phone");

                        if (phone == null || phone.isEmpty()) {
                            showPhoneRequiredDialog();
                            return;
                        }

                        if (address == null || address.isEmpty()) {
                            showAddressDialog();
                        } else {
                            showPaymentMethodDialog();
                        }
                    } else {
                        showPhoneRequiredDialog();
                    }
                }).addOnFailureListener(e -> {
//                    Toast.makeText(CartActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                });
            }
        });
        btnAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddressDialog();
            }
        });
    }

    private void loadCartItems(String userId) {
        db.collection("carts")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                            CartModel cartItem = documentSnapshot.toObject(CartModel.class);
                            cartList.add(cartItem);
                        }
                    }
                    cartAdapter.notifyDataSetChanged();
                    updateTotalPrice();
                })
                .addOnFailureListener(e -> {
                    Log.e("CartActivity", "Lỗi lấy dữ liệu giỏ hàng: " + e.getMessage());
                });
    }
    public void updateTotalPrice() {
        double totalPrice = 0;
        for (CartModel cartItem : cartList) {
            totalPrice += cartItem.getPrice()*cartItem.getQuantity();
        }
        tvPrice.setText(String.format("%.3fđ", totalPrice));
        if (cartList.isEmpty()) {
            btnPay.setEnabled(false);
            btnPay.setBackgroundColor(getResources().getColor(R.color.gray_black)); // màu xám
        } else {
            btnPay.setEnabled(true);
            btnPay.setBackgroundColor(getResources().getColor(R.color.orange)); // màu cam
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        cartList.clear();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        String userId = mAuth.getCurrentUser().getUid();
        loadCartItems(userId);
    }

    private void processOrder() {
        final int[] d = {0};
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String userId = mAuth.getCurrentUser().getUid();

            OrderService orderService = new OrderService();
            OrderModel order = new OrderModel();
            for (CartModel cartItem : cartList) {

                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");

                order = new OrderModel(
                        userId,
                        cartItem.getFoodId(),
                        cartItem.getQuantity(),
                        cartItem.getPrice() * cartItem.getQuantity(),
                        cartItem.getSize(),
                        sdf.format(date),
                        "Chờ xác nhận",
                        tvAddress.getText().toString()
                );
                orderService.addOrder(order).addOnSuccessListener(aVoid -> {
                    d[0]++;
                    if (d[0] == cartList.size()) {

                        loadingOverlay.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        Log.d("CartActivity", "Tất cả đơn hàng đã được thêm");

                         clearCart(userId);

                        Intent intent = new Intent(CartActivity.this, OrderActivity.class);
                        startActivity(intent);
                    }
                }).addOnFailureListener(e -> {
                    Log.e("CartActivity", "Lỗi thêm đơn hàng: "+ e.getMessage() );
                    progressBar.setVisibility(View.GONE);
                    loadingOverlay.setVisibility(View.GONE);
                });
            }

        }
    }

    private void clearCart(String userId) {
        CartService cartService = new CartService();
        cartService.clearCartByUserId(userId);
    }

    private void showPhoneRequiredDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Thiếu số điện thoại")
                .setMessage("Vui lòng cập nhật số điện thoại trước khi thanh toán.")
                .setPositiveButton("OK", null)
                .show();
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
                    ArrayAdapter<String> provinceAdapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item);
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
                                        ArrayAdapter<String> districtAdapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item);
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
                                                            ArrayAdapter<String> wardAdapter = new ArrayAdapter<>(CartActivity.this, android.R.layout.simple_spinner_item);
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
            tvAddress.setText(fullAddress);
            dialog.dismiss();
        });
        dialog.show();
    }
    private void showPaymentMethodDialog() {
        // Inflate layout chọn phương thức thanh toán
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_payment_method, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);

        // Lấy các view từ layout
        RadioGroup radioGroup = dialogView.findViewById(R.id.radioGroupPayment);
        Button btnConfirmPayment = dialogView.findViewById(R.id.btnConfirmPayment);

        // Tạo AlertDialog
        AlertDialog dialog = builder.create();

        // Xử lý khi người dùng nhấn nút xác nhận
        btnConfirmPayment.setOnClickListener(v -> {
            int selectedId = radioGroup.getCheckedRadioButtonId();
            if (selectedId == R.id.radioCashOnDelivery) {
                // Nếu chọn Thanh toán khi nhận hàng
                processOrder(); // Tiến hành xử lý đơn hàng
            } else if (selectedId == R.id.radioTransferBefore) {
                // Nếu chọn Chuyển khoản
                showQRCodeDialog(); // Hiển thị QR code
            } else {
                Toast.makeText(CartActivity.this, "Vui lòng chọn phương thức thanh toán", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        dialog.show();
    }
    private void showQRCodeDialog() {
        // QR Code dữ liệu bạn muốn hiển thị
        String qrData = "https://www.youtube.com/watch?v=enF7gfZgXKE";

        // Tạo một QR Code
        try {
            BitMatrix bitMatrix = new MultiFormatWriter().encode(qrData, BarcodeFormat.QR_CODE, 500, 500);
            Bitmap bitmap = Bitmap.createBitmap(500, 500, Bitmap.Config.RGB_565);
            for (int x = 0; x < 500; x++) {
                for (int y = 0; y < 500; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Tạo một dialog để hiển thị QR Code
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            ImageView imageView = new ImageView(this);
            imageView.setImageBitmap(bitmap);
            builder.setView(imageView);
            builder.setTitle("QR Code Chuyển Khoản");
            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                // Tiến hành chuyển tới trang đơn hàng khi người dùng xác nhận
                processOrder();
            });
            builder.show();
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

}
