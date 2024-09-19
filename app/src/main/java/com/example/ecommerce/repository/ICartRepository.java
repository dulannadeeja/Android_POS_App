package com.example.ecommerce.repository;

import com.example.ecommerce.model.Cart;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface ICartRepository {
    Single<Cart> getCartHandler();
    Completable addProductToCart(int productId);
    Completable removeProductFromCart(int productId) throws Exception;
    Completable clearCart() throws Exception;
    Completable decrementProductQuantity(int productId);
    Single<Boolean> isProductInCart(int productId);
    Single<Boolean> isProductHasStock(int qtyInCart,int productId);
}
