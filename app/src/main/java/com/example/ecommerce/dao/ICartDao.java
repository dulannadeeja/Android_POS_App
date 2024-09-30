package com.example.ecommerce.dao;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;
import java.util.HashMap;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface ICartDao {
    Single<ArrayList<CartItem>> getAllCartItems();
    Completable createCartItem(int productId, int quantity, int newStock, Boolean doNotUpdateStock);
    Completable deleteCartItem(int productId, int newStock);
    Completable updateCartItem(int productId, int updatedQuantity, int newStock);
    Completable clearCart();
    Maybe<CartItem> getCartItem(int productId);
}