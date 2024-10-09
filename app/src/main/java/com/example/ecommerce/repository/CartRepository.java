package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.utils.RoomDBHelper;

import java.util.ArrayList;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class CartRepository implements ICartRepository {

    private ICartDao cartDao;
    private IProductDao productDao;
    private SharedPreferences cartSharedPreferences;

    /**
     * Initializes the CartRepository with the specified database and shared preferences.
     *
     * @param database             The RoomDBHelper instance for accessing DAOs.
     * @param cartSharedPreferences The SharedPreferences for cart data.
     */
    public CartRepository(RoomDBHelper database, SharedPreferences cartSharedPreferences) {
        this.cartDao = database.cartDao();
        this.productDao = database.productDao();
        this.cartSharedPreferences = cartSharedPreferences;
    }

    /**
     * Retrieves the current cart, including its subtotal, taxes, and items.
     *
     * @return A Single emitting the Cart object containing current cart details.
     */
    @Override
    public Single<Cart> getCartHandler() {
        return cartDao.getAllCartItems()
                .flatMap(cartItems -> {
                    // Calculate the subtotal of the cart
                    double cartSubTotalPrice = cartItems.stream()
                            .mapToDouble(cartItem -> cartItem.getQuantity() * (cartItem.getPrice() - cartItem.getDiscount()))
                            .sum();

                    // Placeholder for tax and charges calculation (to be implemented)
                    double cartTotalTaxAndCharges = 0.0;

                    // Build a new Cart object from shared preferences and the retrieved cart items
                    return getCartFromSharedPreferences()
                            .map(cart -> new Cart.CartBuilder()
                                    .withCartSubTotalPrice(cartSubTotalPrice)
                                    .withCartTotalTaxAndCharges(cartTotalTaxAndCharges)
                                    .withOrderId(cart.getOrderId())
                                    .withDiscount(cart.getDiscountId(), cart.getDiscountValue())
                                    .withCartItems(new ArrayList<>(cartItems))
                                    .build());
                });
    }

    /**
     * Saves the specified cart to the database and updates stock levels for each product.
     *
     * This method first clears the existing cart and updates product stock. It then
     * either creates new cart items while updating stock for non-open orders or simply
     * inserts cart items for open orders. Finally, it saves the cart information to shared
     * preferences.
     *
     * @param cart        The cart object containing the items to be saved.
     * @param isOpenOrder Indicates if the order is open (true) or closed (false).
     * @return A Completable indicating the completion status of the save operation.
     */
    @Override
    public Completable saveCartHandler(Cart cart, Boolean isOpenOrder) {
        // Step 1: Clear the cart and update product stock
        return Completable.fromAction(() -> {
                    cartDao.clearCartAndUpdateStock(); // Clear the cart and update stock
                })
                .andThen(clearCartOnSharedPreferences()) // Step 2: Clear cart data in shared preferences
                .andThen(Single.just(cart)) // Step 3: Create a Single from the cart object
                .flatMapCompletable(cart1 -> {
                    Log.d("CartRepository", "Saving cart");

                    ArrayList<CartItem> cartItems = cart1.getCartItems(); // Retrieve cart items

                    // Step 4: Process each cart item
                    return Completable.merge(
                                    cartItems.stream()
                                            .map(cartItem -> productDao.getProductQuantity(cartItem.getProductId())
                                                    .flatMapCompletable(productStock -> {
                                                        if (!isOpenOrder) {
                                                            // For non-open orders, create cart item and update stock
                                                            return Completable.fromAction(() -> {
                                                                cartDao.createCartItemAndUpdateStock(cartItem, productStock - cartItem.getQuantity());
                                                            });
                                                        }
                                                        // For open orders, simply insert the cart item
                                                        return cartDao.insertCartItem(cartItem);
                                                    }))
                                            .collect(Collectors.toList())) // Collect all Completable operations
                            .andThen(saveCartOnSharedPreferences(cart1)); // Step 5: Save the cart in shared preferences
                });
    }

    /**
     * Adds a product to the cart. If the product is already in the cart, it increments the quantity.
     * Otherwise, it adds the product as a new item.
     *
     * @param productId The ID of the product to be added to the cart.
     * @return A Completable indicating the completion status of the add operation.
     */
    @Override
    public Completable addProductToCart(int productId) {
        return productDao.getProductQuantity(productId)
                .flatMapCompletable(productStock -> {
                    Log.d("CartRepository", "Product stock: " + productStock);
                    if (productStock > 0) {
                        return isProductInCart(productId)
                                .flatMapCompletable(isProductInCart -> {
                                    if (isProductInCart) {
                                        Log.d("CartRepository", "Product already in cart");
                                        return cartDao.getCartItem(productId)
                                                .flatMapCompletable(cartItem -> {
                                                    Log.d("CartRepository", "Updating cart item");
                                                    return Completable.fromAction(() -> {
                                                        cartDao.updateCartItemAndStock(productId, cartItem.getQuantity() + 1, productStock - 1);
                                                    });
                                                });
                                    } else {
                                        Log.d("CartRepository", "Adding product to cart");
                                        return productDao.getProductById(productId)
                                                .flatMapCompletable(product -> {
                                                    CartItem cartItem = new CartItem.Builder(productId)
                                                            .withQuantity(1)
                                                            .withPrice(product.getProductPrice())
                                                            .withProductName(product.getProductName())
                                                            .build();
                                                    return Completable.fromAction(() -> {
                                                        cartDao.createCartItemAndUpdateStock(cartItem, productStock - 1);
                                                    });
                                                });
                                    }
                                });
                    } else {
                        return Completable.error(new Exception("Product out of stock"));
                    }
                });
    }

    /**
     * Removes a product from the cart and updates the stock accordingly.
     *
     * @param productId The ID of the product to be removed from the cart.
     * @return A Completable indicating the completion status of the remove operation.
     */
    @Override
    public Completable removeProductFromCart(int productId) {
        return productDao.getProductQuantity(productId)
                .flatMapCompletable(productStock -> cartDao.getCartItem(productId)
                        .flatMapCompletable(cartItem -> Completable.fromAction(() -> {
                            cartDao.deleteCartItemAndUpdateStock(productId, productStock + cartItem.getQuantity());
                        })));
    }

    /**
     * Decrements the quantity of a product in the cart. If the quantity reaches zero,
     * the product is removed from the cart.
     *
     * @param productId The ID of the product to decrement the quantity.
     * @return A Completable indicating the completion status of the decrement operation.
     */
    @Override
    public Completable decrementProductQuantity(int productId) {
        return cartDao.getCartItem(productId)
                .flatMapCompletable(cartItem -> {
                    return productDao.getProductQuantity(productId)
                            .flatMapCompletable(productStock -> {
                                if (cartItem.getQuantity() > 1) {
                                    return Completable.fromAction(() -> {
                                        cartDao.updateCartItemAndStock(productId, cartItem.getQuantity() - 1, productStock + 1);
                                    });
                                } else {
                                    return Completable.fromAction(() -> {
                                        cartDao.deleteCartItemAndUpdateStock(productId, productStock + 1);
                                    });
                                }
                            });
                });
    }

    /**
     * Clears the entire cart and updates the stock for all products.
     *
     * @return A Completable indicating the completion status of the clear operation.
     */
    @Override
    public Completable clearCart() {
        return Completable.fromAction(() -> cartDao.clearCartAndUpdateStock())
                .andThen(clearCartOnSharedPreferences());
    }

    /**
     * Applies a discount to the cart and updates the cart in the database and shared preferences.
     *
     * @param discountId   The ID of the discount to apply.
     * @param discountValue The value of the discount to apply.
     * @return A Completable indicating the completion status of the apply operation.
     */
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
                    return saveCartHandler(updatedCart, false);
                });
    }

    /**
     * Removes any applied discount from the cart and updates the cart in the database and shared preferences.
     *
     * @return A Completable indicating the completion status of the remove operation.
     */
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
                    return saveCartHandler(updatedCart, false);
                });
    }

    /**
     * Saves the specified cart to shared preferences for later retrieval.
     *
     * @param cart The cart object containing the items to be saved.
     * @return A Completable indicating the completion status of the save operation.
     */
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

    /**
     * Retrieves the current cart from shared preferences.
     *
     * @return A Single emitting the Cart object containing current cart details.
     */
    public Single<Cart> getCartFromSharedPreferences() {
        return Single.just(cartSharedPreferences)
                .map(sharedPrefs -> {
                    double cartSubTotalPrice = sharedPrefs.getFloat("cartSubTotalPrice", 0);
                    double cartTotalTaxAndCharges = sharedPrefs.getFloat("cartTotalTaxAndCharges", 0);
                    int orderId = sharedPrefs.getInt("orderId", -1);
                    int discountId = sharedPrefs.getInt("discountId", 0);
                    double discountValue = sharedPrefs.getFloat("discountValue", 0);
                    return new Cart.CartBuilder()
                            .withCartSubTotalPrice(cartSubTotalPrice)
                            .withCartTotalTaxAndCharges(cartTotalTaxAndCharges)
                            .withOrderId(orderId)
                            .withDiscount(discountId, discountValue)
                            .build();
                });
    }

    /**
     * Clears the cart data from shared preferences.
     *
     * @return A Completable indicating the completion status of the clear operation.
     */
    public Completable clearCartOnSharedPreferences() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = cartSharedPreferences.edit();
            editor.clear();
            editor.apply();
        });
    }

    /**
     * Checks if a product is currently in the cart.
     *
     * @param productId The ID of the product to check.
     * @return A Single emitting true if the product is in the cart, false otherwise.
     */
    public Single<Boolean> isProductInCart(int productId) {
        return cartDao.getAllCartItems()
                .map(cartItems -> cartItems.stream()
                        .anyMatch(cartItem -> cartItem.getProductId() == productId));
    }
}
