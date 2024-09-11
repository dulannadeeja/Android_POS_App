package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;

public interface ICartRepository {
    Cart getCart() throws Exception;
    void addProductToCart(int productId) throws Exception;
    void removeProductFromCart(int productId) throws Exception;
    void clearCart() throws Exception;
    void decreaseProductQuantity(int productId) throws Exception;
}
