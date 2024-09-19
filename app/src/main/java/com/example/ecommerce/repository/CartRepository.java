package com.example.ecommerce.repository;

import android.util.Log;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.internal.operators.single.SingleJust;

public class CartRepository implements ICartRepository{

    private ICartDao cartDao;
    private IProductDao productDao;

    public CartRepository(ICartDao cartDao, IProductDao productDao) {
        this.cartDao = cartDao;
        this.productDao = productDao;
    }

    @Override
    public Single<Cart> getCartHandler(){
        return cartDao.getAllCartItems().map(cartItems -> {
            int totalItems = 0;
            double cartSubTotalPrice = 0;
            double cartTotalTax = 0;

            for (CartItem cartItem : cartItems) {
                totalItems += cartItem.getQuantity();
                cartSubTotalPrice += cartItem.getQuantity() * (cartItem.getPrice() - cartItem.getDiscount());
            }
            return new Cart(totalItems,cartSubTotalPrice,cartItems,cartTotalTax,-1,0);
        });
    }

    @Override
    public Completable addProductToCart(int productId) {
        Log.d("CartRepository", "Adding product to cart: " + productId);
        return isProductInCart(productId)
                .flatMapCompletable(isProductInCart -> {
                    // If the product is in the cart, check stock and update quantity
                    if(isProductInCart) {
                        Log.d("CartRepository", "Product already in cart: " + productId);
                        return cartDao.getCartItem(productId)
                                .flatMapCompletable(cartItem -> {
                                    return isProductHasStock(cartItem.getQuantity(), productId)
                                            .flatMapCompletable(isProductHasStock -> {
                                                if (isProductHasStock) {
                                                    Log.d("CartRepository", "Product has stock: " + productId);
                                                    return cartDao.updateCartItem(productId, cartItem.getQuantity() + 1);
                                                } else {
                                                    Log.d("CartRepository", "Product out of stock: " + productId);
                                                    return Completable.error(new Exception("Product out of stock"));
                                                }
                                            });
                                });
                    } else{
                        Log.d("CartRepository", "Product not in cart: " + productId);
                        // If the product is not in the cart, add it
                        return isProductHasStock(1, productId)
                                .flatMapCompletable(isProductHasStock -> {
                                    if (isProductHasStock) {
                                        Log.d("CartRepository", "Product has stock: " + productId);
                                        return cartDao.createCartItem(productId);
                                    } else {
                                        Log.d("CartRepository", "Product out of stock: " + productId);
                                        return Completable.error(new Exception("Product out of stock"));
                                    }
                                });
                    }
                });
    }

    public Single<Boolean> isProductInCart(int productId) {
        return cartDao.getAllCartItems()
                .map(cartItems -> cartItems.stream()
                        .anyMatch(cartItem -> cartItem.getProductId() == productId));
    }

    public Single<Boolean> isProductHasStock(int qtyInCart,int productId) {
        return Single.just(productDao.getProductQuantity(productId) > qtyInCart);
    }

    @Override
    public Completable removeProductFromCart(int productId) throws Exception {
        return cartDao.deleteCartItem(productId);
    }

    @Override
    public Completable decrementProductQuantity(int productId) {
        return cartDao.getCartItem(productId)
                .flatMapCompletable(cartItem -> {
                    if (cartItem.getQuantity() > 1) {
                        return cartDao.updateCartItem(productId, cartItem.getQuantity() - 1);
                    } else {
                        return removeProductFromCart(productId);
                    }
                });
    }

    @Override
    public Completable clearCart() throws Exception {
        return cartDao.clearCart();
    }
}
