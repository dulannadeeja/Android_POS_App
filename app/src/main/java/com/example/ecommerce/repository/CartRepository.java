package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.internal.operators.single.SingleJust;

public class CartRepository implements ICartRepository {

    private ICartDao cartDao;
    private IProductDao productDao;
    private SharedPreferences cartSharedPreferences;

    public CartRepository(ICartDao cartDao, IProductDao productDao, SharedPreferences cartSharedPreferences) {
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.cartSharedPreferences = cartSharedPreferences;
    }

    @Override
    public Single<Cart> getCartHandler() {
        return cartDao.getAllCartItems()
                .flatMap(cartItems -> {
                    // Calculate cart sub-total price
                    double cartSubTotalPrice = cartItems.stream()
                            .mapToDouble(cartItem -> cartItem.getQuantity() * (cartItem.getPrice() - cartItem.getDiscount()))
                            .sum();

                    // Placeholder for tax and charges calculation, can be replaced with actual logic
                    double cartTotalTaxAndCharges = 0.0;

                    // Retrieve cart from shared preferences and build a new cart with updated values
                    return getCartFromSharedPreferences()
                            .map(cart -> new Cart.CartBuilder()
                                    .withCartSubTotalPrice(cartSubTotalPrice)
                                    .withCartTotalTaxAndCharges(cartTotalTaxAndCharges)
                                    .withOrderId(cart.getOrderId())
                                    .withDiscount(cart.getDiscountId(), cart.getDiscountValue())
                                    .withCartItems(cartItems)
                                    .build());
                });
    }

    @Override
    public Completable saveCartHandler(Cart cart) {
        return cartDao.clearCart()
                .andThen(clearCartOnSharedPreferences())
                .andThen(Single.just(cart))
                .flatMapCompletable(cart1 -> {
                    ArrayList<CartItem> cartItems = cart1.getCartItems();
                    return Completable.merge(cartItems.stream()
                                    .map(cartItem -> cartDao.createCartItem(cartItem.getProductId())
                                            .andThen(cartDao.updateCartItem(cartItem.getProductId(), cartItem.getQuantity())))
                                    .collect(Collectors.toList()))
                            .andThen(saveCartOnSharedPreferences(cart1));
                });
    }

    @Override
    public Completable addProductToCart(int productId) {
        return isProductInCart(productId)
                .flatMapCompletable(isProductInCart -> {
                    // If the product is in the cart, check stock and update quantity
                    if (isProductInCart) {
                        return cartDao.getCartItem(productId)
                                .flatMapCompletable(cartItem -> {
                                    return isProductHasStock(cartItem.getQuantity(), productId)
                                            .flatMapCompletable(isProductHasStock -> {
                                                if (isProductHasStock) {
                                                    return cartDao.updateCartItem(productId, cartItem.getQuantity() + 1);
                                                } else {
                                                    return Completable.error(new Exception("Product out of stock"));
                                                }
                                            });
                                });
                    } else {
                        // If the product is not in the cart, add it
                        return isProductHasStock(1, productId)
                                .flatMapCompletable(isProductHasStock -> {
                                    if (isProductHasStock) {
                                        return cartDao.createCartItem(productId);
                                    } else {
                                        return Completable.error(new Exception("Product out of stock"));
                                    }
                                });
                    }
                });
    }

    @Override
    public Completable removeProductFromCart(int productId) {
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
    public Completable clearCart() {
        return cartDao.clearCart().andThen(
                clearCartOnSharedPreferences()
        );
    }

    @Override
    public Completable applyDiscountToCart(int discountId, double discountValue) {
        return getCartHandler()
                .flatMapCompletable(cart -> {
                    Cart updatedCart = new Cart.CartBuilder()
                            .withCartSubTotalPrice(cart.getCartSubTotalPrice())
                            .withCartTotalTaxAndCharges(cart.getCartTotalTaxAndCharges())
                            .withOrderId(cart.getOrderId())
                            .withDiscount(discountId, discountValue)
                            .withCartItems(cart.getCartItems())
                            .build();
                    return saveCartHandler(updatedCart);
                });
    }

    @Override
    public Completable removeDiscountFromCart() {
        return getCartFromSharedPreferences()
                .flatMapCompletable(cart -> {
                    Cart updatedCart = new Cart.CartBuilder()
                            .withCartSubTotalPrice(cart.getCartSubTotalPrice())
                            .withCartTotalTaxAndCharges(cart.getCartTotalTaxAndCharges())
                            .withOrderId(cart.getOrderId())
                            .withDiscount(0, 0)
                            .withCartItems(cart.getCartItems())
                            .build();
                    return saveCartHandler(updatedCart);
                });
    }

    public Completable saveCartOnSharedPreferences(Cart cart) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = cartSharedPreferences.edit();
            editor.putFloat("cartSubTotalPrice", (float) cart.getCartSubTotalPrice());
            editor.putFloat("cartTotalTaxAndCharges", (float) cart.getCartTotalTaxAndCharges());
            editor.putInt("orderId", cart.getOrderId());
            editor.putInt("discountId", cart.getDiscountId());
            editor.putFloat("discountValue", (float) cart.getDiscountValue());
            editor.apply();
        });
    }

    public Single<Cart> getCartFromSharedPreferences() {
        return Single.just(cartSharedPreferences)
                .map(cartSharedPreferences -> {
                    double cartSubTotalPrice = cartSharedPreferences.getFloat("cartSubTotalPrice", 0);
                    double cartTotalTaxAndCharges = cartSharedPreferences.getFloat("cartTotalTaxAndCharges", 0);
                    int orderId = cartSharedPreferences.getInt("orderId", -1);
                    int discountId = cartSharedPreferences.getInt("discountId", 0);
                    double discountValue = cartSharedPreferences.getFloat("discountValue", 0);
                    return new Cart.CartBuilder().withCartSubTotalPrice(cartSubTotalPrice)
                            .withCartTotalTaxAndCharges(cartTotalTaxAndCharges)
                            .withOrderId(orderId)
                            .withDiscount(discountId, discountValue)
                            .build();
                });
    }

    public Completable clearCartOnSharedPreferences() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = cartSharedPreferences.edit();
            editor.clear();
            editor.apply();
        });
    }

    public Single<Boolean> isProductInCart(int productId) {
        return cartDao.getAllCartItems()
                .map(cartItems -> cartItems.stream()
                        .anyMatch(cartItem -> cartItem.getProductId() == productId));
    }

    public Single<Boolean> isProductHasStock(int qtyInCart, int productId) {
        return Single.just(productDao.getProductQuantity(productId) > qtyInCart);
    }
}
