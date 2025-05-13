package com.example.foodorderapp.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.foodorderapp.R;
import com.example.foodorderapp.model.CartModel;
import com.example.foodorderapp.model.FoodModel;
import com.example.foodorderapp.service.CartService;
import com.example.foodorderapp.service.FoodService;
import com.example.foodorderapp.service.UserService;
import com.example.foodorderapp.ui.CartActivity;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartModel> cartList;
    public CartAdapter(Context context, List<CartModel> cartList) {
        this.context = context;
        this.cartList = cartList;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel cartItem = cartList.get(position);

        // Set data for each view
     // Assuming foodId is the food name
        holder.tvPrice.setText(cartItem.getPrice() * cartItem.getQuantity() + "00 đ");
        holder.tvSize.setText(cartItem.getSize());
        holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));

        UserService userService = new UserService();
        CartService cartService = new CartService();
        FoodService foodService = new FoodService();

        foodService.getFoodDetails(cartItem.getFoodId())
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        DocumentSnapshot foodSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                        String imageUrl = foodSnapshot.getString("imageUrl");
                        holder.tvFoodName.setText(foodSnapshot.getString("name"));
                        Map<String, Object> sizePricesMap = (Map<String, Object>) foodSnapshot.get("sizePrices");
                        if (sizePricesMap != null && sizePricesMap.containsKey(cartItem.getSize())) {
                            Object priceObj = sizePricesMap.get(cartItem.getSize());

                            if (priceObj instanceof Number) {
                                Double fetchedPrice = ((Number) priceObj).doubleValue();
                                if (fetchedPrice != null) {
                                    cartItem.setPrice(fetchedPrice);
                                    holder.tvPrice.setText(fetchedPrice * cartItem.getQuantity() + "00 đ");
                                }
                            }
                        }
                        Glide.with(context)
                                .load(imageUrl)
                                .into(holder.imgFood);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("CartAdapter", "Lỗi lấy thông tin món ăn: " + e.getMessage());
                });


        // Set button actions (decrement and increment quantity)
        holder.btnDec.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            if (quantity > 1) {
                cartItem.setQuantity(quantity - 1);
                holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                ((CartActivity) context).updateTotalPrice();
                holder.tvPrice.setText(cartItem.getPrice() * cartItem.getQuantity() + "00 đ");
                cartService.updateCartItem(cartItem.getUserId(), cartItem.getFoodId(), cartItem);
                notifyItemChanged(position);
            } else {
                cartList.remove(position);
                cartService.deleteCartItemByFoodId(cartItem.getUserId(), cartItem.getFoodId(), cartItem.getSize());
                ((CartActivity) context).updateTotalPrice();
                notifyItemRemoved(position);
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity + 1);
            holder.tvPrice.setText(cartItem.getPrice() * cartItem.getQuantity() + "00 đ");
            ((CartActivity) context).updateTotalPrice();
            holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            cartService.updateCartItem(cartItem.getUserId(), cartItem.getFoodId(), cartItem);
            notifyDataSetChanged();
        });

        // Set remove button action
        holder.btnRemove.setOnClickListener(v -> {
            cartList.remove(position);
            cartService.deleteCartItemByFoodId(cartItem.getUserId(), cartItem.getFoodId(), cartItem.getSize());
            ((CartActivity) context).updateTotalPrice();
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return cartList.size();
    }

    // ViewHolder class to hold references to views
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView tvFoodName, tvPrice, tvSize, tvQuantity;
        ImageView imgFood, btnRemove;
        ImageButton btnDec, btnAdd;

        public CartViewHolder(View itemView) {
            super(itemView);
            tvFoodName = itemView.findViewById(R.id.tvFoodName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            imgFood = itemView.findViewById(R.id.imgFood);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            btnDec = itemView.findViewById(R.id.btn_dec);
            btnAdd = itemView.findViewById(R.id.btn_add);
        }
    }
}
