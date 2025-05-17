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
import com.example.foodorderapp.websocket.WebSocketClient;
import com.example.foodorderapp.websocket.WebSocketManager;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

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
        double priceInVND = order.getPrice() * 1000;
        NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
        String formattedPrice = formatter.format(priceInVND);
        holder.tvPrice.setText(formattedPrice + " đ");
        holder.tvQuantity.setText(order.getQuantity()+ " món");
        holder.tvSize.setText(order.getSize());
        holder.tvStatus.setText(order.getStatus());

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
        params.width = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, 60, context.getResources().getDisplayMetrics());
        params.weight = 0;
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

        if (orderList.get(position).getStatus().equals("Chờ xác nhận") || orderList.get(position).getStatus().equals("Đang hủy")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            if (orderList.get(position).getStatus().equals("Chờ xác nhận")) {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
                holder.btnCancel.setEnabled(true);
            }else {
                holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.red));
                holder.btnCancel.setEnabled(false);
            }
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.VISIBLE);
            holder.btnFeedback.setVisibility(View.GONE);
            holder.btnRepurchase.setVisibility(View.GONE);
            holder.tvDate.setVisibility(View.GONE);
            params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
            params.width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics());
            params.weight = 1;
            holder.vBetween.setLayoutParams(params);
            holder.btnCancel.setOnClickListener(v -> {
                new AlertDialog.Builder(context)
                        .setTitle("Xác nhận hủy đơn")
                        .setMessage("Bạn chắc chắn muốn hủy đơn hàng này?")
                        .setPositiveButton("Có", (dialog, which) -> {
                            OrderService orderService = new OrderService();
                            order.setStatus("Đang hủy");
                            orderService.updateOrder(order.getOrderId(), order)
                                    .addOnSuccessListener(aVoid -> {
//                                        orderList.remove(position);
//                                        Date date = new Date();
//                                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
//                                        order.setOrderDate(sdf.format(date));
//                                        ((OrderActivity) context).historyList.add(order);
//                                        notifyItemRemoved(position);
                                        foodService.getStoreIdFromFoodId(order.getFoodId(), storeId -> {
                                            if (storeId != null) {
                                                WebSocketManager.getInstance().sendCancelOrder(storeId, order.getOrderId(), "Khách hàng yêu cầu hủy đơn");
                                            } else {
                                                Log.e("OrderAdapter", "Không tìm thấy storeId từ foodId");
                                            }
                                        });
                                        notifyDataSetChanged();
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage());
                                    });
                        })
                        .setNegativeButton("Không", null)
                        .show();
            });
        } else if (orderList.get(position).getStatus().equals("Đang giao")) {
            holder.tvDate.setVisibility(View.GONE);
            holder.btnFollow.setVisibility(View.VISIBLE);
            holder.tvStatus.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.GONE);
            holder.btnRepurchase.setVisibility(View.GONE);
            params = (LinearLayout.LayoutParams) holder.vBetween.getLayoutParams();
            params.width = (int) TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP, 0, context.getResources().getDisplayMetrics());
            params.weight = 1;
            holder.vBetween.setLayoutParams(params);

//            holder.btnCancel.setOnClickListener(v -> {
//                OrderService orderService = new OrderService();
//                foodService.getStoreIdFromFoodId(order.getFoodId(), storeId -> {
//                    if (storeId != null) {
//                        WebSocketManager.getInstance().sendCancelOrder(storeId, order.getOrderId(), "Khách hàng yêu cầu hủy đơn");
//                    } else {
//                        Log.e("OrderAdapter", "Không tìm thấy storeId từ foodId");
//                    }
//                });
//
////                // Lắng nghe tin nhắn từ WebSocket
////                WebSocketManager.getInstance().setOnMessageListener(message -> {
////                    try {
////                        JSONObject json = new JSONObject(message);
////                        String type = json.optString("type");
////                        String orderIdFromServer = json.optString("orderId");
////
////                        if ("accept_cancel".equals(type) && orderIdFromServer.equals(order.getOrderId())) {
////                            order.setStatus("Đã hủy");
////
////                            Date date = new Date();
////                            SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
////                            order.setOrderDate(sdf.format(date));
////
////                            orderService.updateOrder(order.getOrderId(), order)
////                                    .addOnSuccessListener(aVoid2 -> {
////                                        ((Activity) context).runOnUiThread(() -> {
////                                            notifyItemChanged(position);
////                                            if (context instanceof OrderActivity) {
////                                                ((OrderActivity) context).historyList.add(order);
////                                            }
////                                        });
////                                    });
////                        }
////                    } catch (Exception e) {
////                        e.printStackTrace();
////                    }
////                });
////                order.setStatus("Đang chờ hủy");
////                orderService.updateOrder(order.getOrderId(), order)
////                        .addOnSuccessListener(aVoid -> {
////                            notifyItemChanged(position);
////
////                            foodService.getStoreIdFromFoodId(order.getFoodId(), storeId -> {
////                                if (storeId != null) {
//                                    WebSocketManager.getInstance().sendCancelOrder(storeId, order.getOrderId(), "Khách hàng yêu cầu hủy đơn");
////                                } else {
////                                    Log.e("OrderAdapter", "Không tìm thấy storeId từ foodId");
////                                }
////                            });
////
////                            // Lắng nghe tin nhắn từ WebSocket
////                            WebSocketManager.getInstance().setOnMessageListener(message -> {
////                                try {
////                                    JSONObject json = new JSONObject(message);
////                                    String type = json.optString("type");
////                                    String orderIdFromServer = json.optString("orderId");
////
////                                    if ("accept_cancel".equals(type) && orderIdFromServer.equals(order.getOrderId())) {
////                                        order.setStatus("Đã hủy");
////
////                                        Date date = new Date();
////                                        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm");
////                                        order.setOrderDate(sdf.format(date));
////
////                                        orderService.updateOrder(order.getOrderId(), order)
////                                                .addOnSuccessListener(aVoid2 -> {
////                                                    ((Activity) context).runOnUiThread(() -> {
////                                                        notifyItemChanged(position);
////                                                        if (context instanceof OrderActivity) {
////                                                            ((OrderActivity) context).historyList.add(order);
////                                                        }
////                                                    });
////                                                });
////                                    }
////                                } catch (Exception e) {
////                                    e.printStackTrace();
////                                }
////                            });
////                        })
////                        .addOnFailureListener(e -> {
////                            Log.e("OrderAdapter", "Lỗi cập nhật trạng thái: " + e.getMessage());
////                        });
//            });
        } else if (orderList.get(position).getStatus().equals("Hoàn thành")) {
            holder.tvStatus.setVisibility(View.VISIBLE);
            holder.tvStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green));
            holder.btnFollow.setVisibility(View.GONE);
            holder.btnCancel.setVisibility(View.GONE);
            holder.btnFeedback.setVisibility(View.VISIBLE);
            holder.btnRepurchase.setVisibility(View.VISIBLE);
            holder.tvDate.setText(orderList.get(position).getOrderDate());
            holder.tvDate.setVisibility(View.VISIBLE);
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
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_map_route, null);
        MapView mapView = dialogView.findViewById(R.id.dialog_map_view);
        mapView.onCreate(null); // No savedInstanceState
        mapView.onResume();     // Ensure the map is visible immediately

        AlertDialog dialog = new AlertDialog.Builder(context)
                .setView(dialogView)
                .setNegativeButton("Đóng", (d, which) -> d.dismiss())
                .create();
        dialog.show();

        MapService mapService = new MapService();

        mapService.getCoordinatesFromAddress(userAddress, (userLat, userLng) -> {
            if (userLat == 0 && userLng == 0) return;

            mapService.getCoordinatesFromAddress(shopAddress, (shopLat, shopLng) -> {
                if (shopLat == 0 && shopLng == 0) return;

                ((Activity) context).runOnUiThread(() -> {
                    mapView.getMapAsync(googleMap -> {
                        googleMap.clear();

                        LatLng userLatLng = new LatLng(userLat, userLng);
                        LatLng shopLatLng = new LatLng(shopLat, shopLng);

                        googleMap.addMarker(new MarkerOptions().position(userLatLng).title("Bạn"));
                        googleMap.addMarker(new MarkerOptions().position(shopLatLng).title("Cửa hàng"));

                        LatLngBounds bounds = new LatLngBounds.Builder()
                                .include(userLatLng)
                                .include(shopLatLng)
                                .build();
                        googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));

                        mapService.getRouteCoordinatesOSRM(userLat, userLng, shopLat, shopLng, coordinates -> {
                            if (coordinates != null) {
                                Log.d("RouteDebug", "Coordinates received: " + coordinates.toString());
                                PolylineOptions routeLine = new PolylineOptions().color(Color.BLUE).width(8);
                                try {
                                    for (int i = 0; i < coordinates.length(); i++) {
                                        JSONArray point = coordinates.getJSONArray(i);
                                        double lng = point.getDouble(0);
                                        double lat = point.getDouble(1);
                                        routeLine.add(new LatLng(lat, lng));
                                    }
                                    ((Activity) context).runOnUiThread(() -> {
                                        googleMap.addPolyline(routeLine);
                                    });
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                Log.d("RouteDebug", "No coordinates received.");
                            }
                        });
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
