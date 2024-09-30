package com.example.ecommerce.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
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
import com.example.ecommerce.features.products.ProductsViewModel;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.features.products.OnItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;


public class ProductsAdapter extends RecyclerView.Adapter<ProductsAdapter.ViewHolder> {

    private final OnItemClickListener listener;
    private final Context context;
    private ArrayList<Product> products = new ArrayList<Product>();
    private HashMap<Integer, Integer> cartQuantityMap = new HashMap<>();

    public ProductsAdapter(OnItemClickListener listener, Context context, ArrayList<Product> products) {
        this.listener = listener;
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public ProductsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.products_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductsAdapter.ViewHolder holder, int position) {
        if(products.get(position).getProductImage() != null) {
            Glide.with(context)
                    .load(products.get(position).getProductImage())
                    .into(holder.ivProductImage);
        }else{
            holder.ivProductImage.setImageResource(R.drawable.product_image_placeholder);
        }
        holder.tvProductName.setText(products.get(position).getProductName());
        holder.tvProductPrice.setText(String.valueOf(products.get(position).getProductPrice()));
        holder.tvProductQuantity.setText(String.valueOf(products.get(position).getProductQuantity()));

        int productId = products.get(position).get_productId();
        if (cartQuantityMap.containsKey(productId) && cartQuantityMap.get(productId) > 0) {
            holder.tvCartQuantity.setText(String.valueOf(cartQuantityMap.get(productId)));
            holder.tvCartQuantity.setVisibility(View.VISIBLE);
        } else {
            holder.tvCartQuantity.setText("0");
            holder.tvCartQuantity.setVisibility(View.GONE);
        }

        if(products.get(position).getProductQuantity() <= 5){
            holder.productInfo.setImageResource(R.drawable.baseline_info_outline_24_red);
        }else{
            holder.productInfo.setImageResource(R.drawable.baseline_info_outline_24);
        }

        holder.bind(products.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivProductImage;
        private final TextView tvProductName, tvProductPrice, tvProductQuantity, tvCartQuantity;
        private final ConstraintLayout clProductItem;
        private final ImageView productInfo;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            clProductItem = itemView.findViewById(R.id.product_Item);
            ivProductImage = itemView.findViewById(R.id.iv_product_image);
            tvProductName = itemView.findViewById(R.id.tv_product_name);
            tvProductPrice = itemView.findViewById(R.id.tv_product_price);
            tvProductQuantity = itemView.findViewById(R.id.tv_product_quantity);
            productInfo = itemView.findViewById(R.id.product_info);
            tvCartQuantity = itemView.findViewById(R.id.tv_cart_quantity);

        }

        public void bind (final Product item, final OnItemClickListener listener) {
            clProductItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item.get_productId());
                }
            });

            clProductItem.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View v) {
                    listener.onItemLongClick(item.get_productId());
                    return true;
                }
            });

            productInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onProductInfoClick(item);
                }
            });

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setProducts(ArrayList<Product> products) {
        this.products.clear();
        if (products != null && !products.isEmpty()) {
            this.products.addAll(products);
        }
        notifyDataSetChanged();
    }

    public void onProductChange(HashMap<Integer, Integer> updatedCartQuantityMap, HashMap<Integer, Integer> productStockMap, ArrayList<Integer> productIdsToUpdate) {
        cartQuantityMap.clear();
        cartQuantityMap.putAll(updatedCartQuantityMap);
        productStockMap.forEach((productId, stock) -> {
            products.stream()
                    .filter(product -> product.get_productId() == productId)
                    .forEach(product -> product.setProductQuantity(stock));
        });
        productIdsToUpdate.forEach(productId -> {
            int position = getProductPosition(productId);
            if (position != -1) {
                notifyItemChanged(position);
            }
        });
    }

    // Helper method to get the position of the product in the list
    private int getProductPosition(int productId) {
        for (int i = 0; i < products.size(); i++) {
            if (products.get(i).get_productId() == productId) {
                return i;
            }
        }
        return -1;  // Return -1 if product not found
    }
}
