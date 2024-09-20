package com.example.ecommerce.dao;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface ICartDao {
    Single<ArrayList<CartItem>> getAllCartItems();
    Completable createCartItem(int productId);
    Completable deleteCartItem(int productId);
    Completable updateCartItem(int productId, int quantity);
    Completable clearCart();
    Maybe<CartItem> getCartItem(int productId);
}