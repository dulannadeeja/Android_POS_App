package com.example.ecommerce.dao;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;

public interface ICartDao {
    ArrayList<CartItem> getCartItems() throws Exception;
    void createCartItem(int productId) throws Exception;
    void deleteCartItem(int productId) throws Exception;
    void updateCartItem(int productId, int quantity) throws Exception;
    void clearCart() throws Exception;
    CartItem getCartItem(int productId) throws Exception;
}
