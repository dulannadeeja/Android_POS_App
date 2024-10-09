package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.CartProduct;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ICartDao {

    // --- Insert operations ---

    /**
     * Insert a CartItem into the cart. Replaces existing item in case of conflict.
     *
     * @param cartItem the cart item to insert
     * @return Completable indicating the completion status of the operation.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCartItem(CartItem cartItem);

    // --- Update operations ---

    /**
     * Update the stock of a product based on productId.
     *
     * @param productId the ID of the product
     * @param newStock  the updated stock value
     * @return Completable indicating the completion status of the operation.
     */
    @Query("UPDATE products SET product_quantity = :newStock WHERE product_id = :productId")
    Completable updateProductStock(int productId, int newStock);

    /**
     * Update the quantity of an item in the cart.
     *
     * @param productId       the ID of the product
     * @param updatedQuantity the updated quantity of the product
     * @return Completable indicating the completion status of the operation.
     */
    @Query("UPDATE cart_items SET quantity = :updatedQuantity WHERE product_id = :productId")
    Completable updateCartItem(int productId, int updatedQuantity);

    // --- Delete operations ---

    /**
     * Delete a cart item based on its productId.
     *
     * @param productId the ID of the product
     * @return Completable indicating the completion status of the operation.
     */
    @Query("DELETE FROM cart_items WHERE product_id = :productId")
    Completable deleteCartItem(int productId);

    // --- Retrieve operations ---

    /**
     * Retrieve all cart items.
     *
     * @return Single emitting a list of all cart items.
     */
    @Query("SELECT * FROM cart_items")
    Single<List<CartItem>> getAllCartItems();

    /**
     * Retrieve a cart item by its productId.
     *
     * @param productId the ID of the product
     * @return Maybe emitting the CartItem if found, or completing if not.
     */
    @Query("SELECT * FROM cart_items WHERE product_id = :productId")
    Maybe<CartItem> getCartItem(int productId);

    /**
     * Retrieve all cart products and their stock by joining cart_items and products tables.
     *
     * @return Single emitting a list of CartProduct objects containing the product and stock information.
     */
    @Query("SELECT ci.product_id AS cart_product_id, " +
            "ci.quantity AS cart_product_quantity, " +
            "p.product_quantity AS product_stock " +
            "FROM cart_items ci " +
            "INNER JOIN products p ON ci.product_id = p.product_id")
    Single<List<CartProduct>> getCartProductsStock();

    // --- Transactional operations ---

    /**
     * Inserts a cart item and updates the stock of the corresponding product in a transaction.
     *
     * @param cartItem the cart item to insert
     * @param newStock the updated stock of the product
     */
    @Transaction
    default void createCartItemAndUpdateStock(CartItem cartItem, int newStock) {
        try {
            // Insert the cart item into the database synchronously
            insertCartItem(cartItem).blockingAwait();
            // Update the product stock synchronously
            updateProductStock(cartItem.getProductId(), newStock).blockingAwait();
        } catch (Exception e) {
            throw new RuntimeException("Error creating cart item and updating stock", e);
        }
    }

    /**
     * Delete a cart item and update the stock of the corresponding product.
     *
     * @param productId the ID of the product
     * @param newStock  the updated stock of the product
     */
    @Transaction
    default void deleteCartItemAndUpdateStock(int productId, int newStock) {
        try {
            // Retrieve the cart item to be deleted
            CartItem cartItem = getCartItem(productId).blockingGet();
            // Delete the cart item
            deleteCartItem(cartItem.getProductId()).blockingAwait();
            // Update the stock of the corresponding product
            updateProductStock(productId, newStock).blockingAwait();
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cart item and updating stock", e);
        }
    }

    /**
     * Update the quantity of a cart item and the stock of the corresponding product.
     *
     * @param productId       the ID of the product
     * @param updatedQuantity the updated quantity in the cart
     * @param newStock        the updated stock of the product
     */
    @Transaction
    default void updateCartItemAndStock(int productId, int updatedQuantity, int newStock) {
        try {
            // Update the cart item quantity
            updateCartItem(productId, updatedQuantity).blockingAwait();
            // Update the stock of the corresponding product
            updateProductStock(productId, newStock).blockingAwait();
        } catch (Exception e) {
            throw new RuntimeException("Error updating cart item and stock", e);
        }
    }

    /**
     * Clear the cart and update the stock of all products in the cart.
     * Retrieves all cart items, updates stock for each product, and deletes the cart items.
     */
    @Transaction
    default void clearCartAndUpdateStock() {
        try {
            List<CartProduct> cartProducts = getCartProductsStock().blockingGet();
            for (CartProduct cartProduct : cartProducts) {
                // Update product stock
                updateProductStock(cartProduct.getCartProductId(),
                        cartProduct.getProductStock() + cartProduct.getCartProductQuantity()).blockingAwait();
                // Delete the cart item
                deleteCartItem(cartProduct.getCartProductId()).blockingAwait();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error clearing cart and updating stock", e);
        }
    }
}
