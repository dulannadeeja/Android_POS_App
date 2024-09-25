package com.example.ecommerce.features.cart;

public interface OnCartItemClickListener {
    void onCartItemClick(int cartItemId);
    void onCartItemLongClick(int cartItemId);
    void onCartItemAddClick(int cartItemId);
    void onCartItemRemoveClick(int cartItemId);
}
