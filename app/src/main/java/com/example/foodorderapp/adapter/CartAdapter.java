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

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<CartModel> cartList;
    private Double price = 0.0;
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
        holder.tvPrice.setText(cartItem.getPrice() + " đ");
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
                        Double fetchedPrice = foodSnapshot.getDouble("price");

                        if (fetchedPrice != null) {
                            price = fetchedPrice;
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
                cartItem.setPrice(price * cartItem.getQuantity());
                holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
                ((CartActivity) context).updateTotalPrice();
                holder.tvPrice.setText(price * cartItem.getQuantity()+" đ");
                cartService.updateCartItem(cartItem.getUserId(), cartItem.getFoodId(), cartItem);
                notifyItemChanged(position);

            } else {
                cartList.remove(position);
                cartService.deleteCartItemByFoodId(cartItem.getUserId(), cartItem.getFoodId());
                notifyItemRemoved(position);
            }
        });

        holder.btnAdd.setOnClickListener(v -> {
            int quantity = cartItem.getQuantity();
            cartItem.setQuantity(quantity + 1);
            cartItem.setPrice(price * cartItem.getQuantity());
            holder.tvPrice.setText(price * cartItem.getQuantity() + " đ");
            ((CartActivity) context).updateTotalPrice();
            holder.tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            cartService.updateCartItem(cartItem.getUserId(), cartItem.getFoodId(), cartItem);
            notifyItemChanged(position);
        });

        // Set remove button action
        holder.btnRemove.setOnClickListener(v -> {
            cartList.remove(position);
            cartService.deleteCartItemByFoodId(cartItem.getUserId(), cartItem.getFoodId());
            notifyItemRemoved(position);
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
