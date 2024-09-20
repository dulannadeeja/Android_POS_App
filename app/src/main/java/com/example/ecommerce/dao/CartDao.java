package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class CartDao implements ICartDao {

    private final DatabaseHelper databaseHelper;

    public CartDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    @SuppressLint("Range")
    public Single<ArrayList<CartItem>> getAllCartItems() {
        return Single.fromCallable(() -> {
            ArrayList<CartItem> cartItems = new ArrayList<>();
            // SQL query to join cart items and product details in one query
            String query = "SELECT ci." + DatabaseHelper.COLUMN_CART_ITEM_ID + ", " +
                    "ci." + DatabaseHelper.COLUMN_PRODUCT_ID + ", " +
                    "ci." + DatabaseHelper.COLUMN_QUANTITY + ", " +
                    "p." + DatabaseHelper.COLUMN_PRODUCT_PRICE + ", " +
                    "p." + DatabaseHelper.COLUMN_PRODUCT_DISCOUNT + ", " +
                    "p." + DatabaseHelper.COLUMN_PRODUCT_NAME +
                    " FROM " + DatabaseHelper.TABLE_CART_ITEMS + " ci " +
                    "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p " +
                    "ON ci." + DatabaseHelper.COLUMN_PRODUCT_ID + " = p." + DatabaseHelper.COLUMN_PRODUCT_ID;

            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery(query, null);
            try  {

                // Iterate over the cursor to fetch cart items
                while (cursor.moveToNext()) {
                    int cartItemId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_ITEM_ID));
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                    int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    double discount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT));
                    String productName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));

                    // Add each cart item to the list
                    CartItem cartItem = new CartItem(cartItemId, productId, quantity, price, discount, productName);
                    cartItems.add(cartItem);
                }
            } catch (Exception e) {
                // Let RxJava propagate the error
                throw new RuntimeException("Error getting cart items", e);
            } finally {
                cursor.close();
            }
            return cartItems;
        });
    }

    @Override
    public Completable createCartItem(int productId) {
        return Completable.fromAction(() -> {
            try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
                Log.d("CartDao", "Creating cart item for product: " + productId);
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_CART_ITEMS + " (" + DatabaseHelper.COLUMN_PRODUCT_ID + ", " + DatabaseHelper.COLUMN_QUANTITY + ") VALUES (" + productId + ", 1)");
            } catch (Exception e) {
                Log.e("CartDao", "Error creating cart item", e);
                throw new RuntimeException("Error creating cart item", e);
            }
        });
    }

    @Override
    public Completable deleteCartItem(int productId) {
        return Completable.fromAction(()->{
            try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
                db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId);
            } catch (Exception e) {
                throw new RuntimeException("Error deleting cart item", e);
            }
        });
    }

    @Override
    public Completable updateCartItem(int productId, int quantity){
        return Completable.fromAction(() -> {
            try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_CART_ITEMS + " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + quantity + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId);
            } catch (Exception e) {
                throw new RuntimeException("Error updating cart item", e);
            }
        });
    }

    @Override
    public Completable clearCart() {
        return Completable.fromAction(() -> {
            try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
                db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS);
            } catch (Exception e) {
                throw new RuntimeException("Error clearing cart", e);
            }
        });
    }

    @Override
    @SuppressLint("Range")
    public Maybe<CartItem> getCartItem(int productId){
        return Maybe.create(emitter -> {
            try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
                Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                if (cursor.moveToFirst()) {
                    int _cartItemId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_ITEM_ID));
                    int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY));

                    Cursor productCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                    productCursor.moveToFirst();
                    double price = productCursor.getDouble(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    double discount = productCursor.getDouble(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT));
                    String productName = productCursor.getString(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
                    productCursor.close();
                    CartItem cartItem = new CartItem(_cartItemId, productId, quantity, price, discount, productName);
                    emitter.onSuccess(cartItem);
                } else {
                    emitter.onComplete();
                }
            } catch (Exception e) {
                emitter.onError(e);
            }
        });
    }

}
