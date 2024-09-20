package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface ICartRepository {
    Single<Cart> getCartHandler();
    Completable saveCartHandler(Cart cart);
    Completable addProductToCart(int productId);
    Completable removeProductFromCart(int productId);
    Completable clearCart();
    Completable decrementProductQuantity(int productId);
    Single<Boolean> isProductInCart(int productId);
    Single<Boolean> isProductHasStock(int qtyInCart,int productId);
    Completable applyDiscountToCart(int discountId, double discountValue);
    Completable clearCartOnSharedPreferences();
    Completable removeDiscountFromCart();
}
