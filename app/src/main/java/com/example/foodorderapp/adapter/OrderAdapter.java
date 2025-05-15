package com.example.foodorderapp.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.model.OrderModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.MapService;
import com.example.foodorderapp.service.OrderService;
import com.example.foodorderapp.service.ShopService;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.CartActivity;
import com.example.foodorderapp.ui.HomeActivity;
import com.example.foodorderapp.ui.OrderActivity;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;
import com.google.type.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder> {
    private ArrayList<OrderModel> orderList;
    private Context context;
    public OrderAdapter(ArrayList<OrderModel> orderList, Context context) {
        this.orderList = orderList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_food, parent, false);

        return new OrderAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderAdapter.ViewHolder holder, int position) {
        OrderModel order = orderList.get(position);

        FoodService foodService = new FoodService();
        // Gọi FoodService để lấy thông tin món ăn
        foodService.getFoodDetails(order.getFoodId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot doc = queryDocumentSnapshots.getDocuments().get(0);
                        String name = doc.getString("name");
                        String imageUrl = doc.getString("imageUrl");

                        holder.tvName.setText(name);

                        Glide.with(context)
                                .load(imageUrl)
//                                .placeholder(R.drawable.loading)          // Ảnh tạm trong lúc load
//                                .error(R.drawable.default_image)          // Ảnh lỗi nếu load fail
                                .into(holder.image);
                    }
                });
        holder.tvId.setText("#"+order.getOrderId());
        holder.tvPrice.setText(order.getPrice() + "00 đ");
        holder.tvQuantity.setText(order.getQuantity()+ " món");
        holder.tvSize.setText(order.getSize());
        holder.tvStatus.setText(order.getStatus());
        holder.tvDate.setVisibility(View.GONE);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.btnCancel.setVisibility(View.VISIBLE);
        holder.tvStatus.setVisibility(View.GONE);

        holder.vBetween.setVisibility(View.VISIBLE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
        holder.vBetween.setLayoutParams(params);

        holder.btnFollow.setOnClickListener(v -> {
            UserService userService = new UserService();
            userService.getUserAddress().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    String userAddress = task.getResult();

                    new FoodService().getFoodDetails(order.getFoodId()).addOnCompleteListener(foodTask -> {
                        if (foodTask.isSuccessful() && foodTask.getResult() != null && !foodTask.getResult().isEmpty()) {
                            DocumentSnapshot doc = foodTask.getResult().getDocuments().get(0);
                            FoodModel food = doc.toObject(FoodModel.class);
                            if (food != null) {
                                int storeId = doc.getLong("storeId").intValue();
                                new ShopService().getShopById(storeId).addOnCompleteListener(shopTask -> {
                                    if (shopTask.isSuccessful() && shopTask.getResult() != null && !shopTask.getResult().isEmpty()) {
                                        DocumentSnapshot shopDoc = shopTask.getResult().getDocuments().get(0);
                                        String shopAddress = shopDoc.getString("address");
                                        if (shopAddress != null && !shopAddress.isEmpty()) {
                                            // Đã có userAddress và shopAddress
                                            showRouteDialog(userAddress, shopAddress);
                                        }
                                    }
                                });
                            }
                        }
                    });
                }
            });
        });

        holder.btnCancel.setOnClickListener(v -> {
            OrderService orderService = new OrderService();
            order.setStatus("Đã hủy");
            orderService.updateOrder(order.getOrderId(), order)
                    .addOnSuccessListener(aVoid -> {
                        orderList.remove(position);

                        Date date = new Date();
                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
                        order.setOrderDate(sdf.format(date));

                        ((OrderActivity) context).historyList.add(order);

                        notifyItemRemoved(position);
                        notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage());
                    });
        });

        if (orderList.get(position).getStatus().equals("Hoàn thành")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvDate.setText(orderList.get(position).getOrderDate());
            holder.tvDate.setVisibility(View.VISIBLE);
//            params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
//            params.width = (int) TypedValue.applyDimension(
//                    TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics());
//            params.weight = 1;
//            holder.vBetween.setLayoutParams(params);

        } else  if (orderList.get(position).getStatus().equals("Đã hủy")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.GONE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
            holder.tvDate.setText(orderList.get(position).getOrderDate());
            holder.tvDate.setVisibility(View.VISIBLE);
            params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
            params.width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics());
            params.weight = 1;
            holder.vBetween.setLayoutParams(params);
        }

        holder.btnRepurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserService userService = new UserService();
                CartService cartService = new CartService();

                userService.getUser().addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        String userId = task.getResult().getId();
                        CartModel newCartItem = new CartModel(order.getFoodId(), order.getSize(), order.getQuantity(), order.getPrice(), userId);
                        cartService.checkAndAddOrUpdateCartItem(newCartItem)
                                .addOnSuccessListener(aVoid -> {
                                    Intent intent = new Intent((Activity) context, CartActivity.class);
                                    context.startActivity(intent);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Lỗi thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
                                });
                    } else {
                    }
                });
            }
        });
    }
    private void showRouteDialog(String userAddress, String shopAddress) {
        if (!(context instanceof androidx.fragment.app.FragmentActivity)) return;
        FragmentActivity activity = (androidx.fragment.app.FragmentActivity) context;
        FragmentManager fm = activity.getSupportFragmentManager();

        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_map_route, null);

        SupportMapFragment mapFragment = (SupportMapFragment) fm.findFragmentById(R.id.dialog_map_fragment);
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.dialog_map_fragment, mapFragment).commit();
        }

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setTitle("Đường đi đến cửa hàng")
                .setView(dialogView)
                .setNegativeButton("Đóng", (d, which) -> d.dismiss())
                .create();

        dialog.show();

        MapService mapService = new MapService();

        // Lấy tọa độ user
        SupportMapFragment finalMapFragment = mapFragment;
        mapService.getCoordinatesFromAddress(userAddress, (userLat, userLng) -> {
            if (userLat == 0 && userLng == 0) {
                return;
            }

            // Lấy tọa độ shop
            mapService.getCoordinatesFromAddress(shopAddress, (shopLat, shopLng) -> {
                if (shopLat == 0 && shopLng == 0) {
                    return;
                }

                // Hiển thị map và đường đi trên UI thread
                ((Activity) context).runOnUiThread(() -> {
                    finalMapFragment.getMapAsync(googleMap -> {
                        googleMap.clear();

                        com.google.android.gms.maps.model.LatLng userLatLng = new com.google.android.gms.maps.model.LatLng(userLat, userLng);
                        com.google.android.gms.maps.model.LatLng shopLatLng = new com.google.android.gms.maps.model.LatLng(shopLat, shopLng);

                        // Thêm marker user và shop
                        googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Bạn"));
                        googleMap.addMarker(new MarkerOptions().position(shopLatLng).title("Cửa hàng"));

                        // Zoom bản đồ để hiện cả 2 điểm
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(userLatLng);
                        builder.include(shopLatLng);
                        LatLngBounds bounds = builder.build();
                        int padding = 100; // padding cho bản đồ
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                        // Vẽ đường đi giữa 2 điểm (đơn giản dạng đường thẳng)
                        PolylineOptions polylineOptions = new PolylineOptions()
                                .add(userLatLng)
                                .add(shopLatLng)
                                .color(Color.BLUE)
                                .width(8);
                        googleMap.addPolyline(polylineOptions);

                        // Nếu muốn bạn có thể dùng MapService.getTravelTimeOSRM để lấy thời gian và hiển thị
                    });
                });
            });
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView tvName, tvTitle, tvPrice, tvQuantity, tvId, tvStatus, tvDate, tvSize;
        public ImageView image;
        public View vBetween;
        public Button btnFollow, btnCancel, btnFeedback, btnRepurchase;

        public ViewHolder(View view) {
            super(view);

            tvName = view.findViewById(R.id.tv_name_food);
            tvTitle = view.findViewById(R.id.tv_title);
            tvPrice = view.findViewById(R.id.tv_price);
            tvQuantity = view.findViewById(R.id.tv_quantity);
            tvId = view.findViewById(R.id.tv_id);
            tvSize = view.findViewById(R.id.tv_size);
            tvDate = view.findViewById(R.id.tv_date);
            image = view.findViewById(R.id.img_food);
            tvStatus = view.findViewById(R.id.tv_status);
            btnFollow = view.findViewById(R.id.btn_follow);
            btnCancel = view.findViewById(R.id.btn_cancel);
            btnFeedback = view.findViewById(R.id.btn_feedback);
            btnRepurchase = view.findViewById(R.id.btn_repurchase);
            vBetween = view.findViewById(R.id.v_between);
        }
    }
}
