package com.example.ecommerce.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.ecommerce.R;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.ui.cart.OnCartItemClickListener;
import com.example.ecommerce.ui.products.OnItemClickListener;

import java.util.ArrayList;


public class CartItemsAdapter extends RecyclerView.Adapter<CartItemsAdapter.ViewHolder> {

    private final OnCartItemClickListener listener;
    private final Context context;
    private ArrayList<CartItem> cartItems = new ArrayList<CartItem>();

    public CartItemsAdapter(OnCartItemClickListener listener, Context context, ArrayList<CartItem> cartItems) {
        this.listener = listener;
        this.context = context;
        this.cartItems = cartItems;
    }

    @NonNull
    @Override
    public CartItemsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartItemsAdapter.ViewHolder holder, int position) {
        holder.tvCartItemTitle.setText(cartItems.get(position).getProductName());
        String totalPrice = String.valueOf(cartItems.get(position).getPrice() * cartItems.get(position).getQuantity());
        holder.tvCartItemPrice.setText(totalPrice);
        String quantity = String.valueOf(cartItems.get(position).getQuantity());
        holder.tvCartItemQuantity.setText(String.format("Qty: %s", quantity));
        holder.bind(cartItems.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvCartItemTitle, tvCartItemPrice, tvCartItemQuantity;
        private final ConstraintLayout clCartItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            clCartItem = itemView.findViewById(R.id.cl_cart_item);
            tvCartItemTitle = itemView.findViewById(R.id.cart_item_title);
            tvCartItemPrice = itemView.findViewById(R.id.cart_item_total);
            tvCartItemQuantity = itemView.findViewById(R.id.cart_item_quantity);
        }

        public void bind (final CartItem item, final OnCartItemClickListener listener) {
            clCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onCartItemClick(item.getProductId());
                }
            });

            clCartItem.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    listener.onCartItemLongClick(item.getProductId());
                    return true;
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
        notifyDataSetChanged();
    }
}
