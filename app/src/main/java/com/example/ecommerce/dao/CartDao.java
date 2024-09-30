package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteTransactionListener;
import android.util.Log;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.HashMap;

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
            try {

                // Iterate over the cursor to fetch cart items
                while (cursor.moveToNext()) {
                    int cartItemId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_ITEM_ID));
                    int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                    int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY));
                    double price = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                    double discount = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT));
                    String productName = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));

                    // Add each cart item to the list
                    CartItem cartItem = new CartItem.Builder(productId)
                            .withCartItemId(cartItemId)
                            .withQuantity(quantity)
                            .withPrice(price)
                            .withDiscount(discount)
                            .withProductName(productName)
                            .build();

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
    public Completable createCartItem(int productId, int quantity, int newStock, Boolean doNotUpdateStock) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = null;
            try {
                db = databaseHelper.getWritableDatabase();
                db.beginTransaction();
                Log.d("CartDao", "createCartItem: productId: " + productId + " quantity: " + quantity + " newStock: " + newStock);
                db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_CART_ITEMS + " (" + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + ", " + DatabaseHelper.COLUMN_QUANTITY_CART_ITEMS + ") VALUES (" + productId + ", " + quantity + ")");
                if (!doNotUpdateStock) {
                    Log.d("CartDao", "createCartItem: updating stock");
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, newStock);
                    db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                    // retrieve the stock of the product
                    Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_PRODUCT_QUANTITY + " FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                    cursor.moveToFirst();
                    @SuppressLint("Range") int stock = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
                    cursor.close();
                    Log.d("CartDao", "Stock after created cartItem: " + stock);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new RuntimeException("Error creating cart item", e);
            }finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        });
    }

    @Override
    public Completable deleteCartItem(int productId, int newStock) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = null;
            try {
                db = databaseHelper.getWritableDatabase();
                db.beginTransaction();
                db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + " = " + productId);
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, newStock);
                db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new RuntimeException("Error deleting cart item", e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        });
    }

    @Override
    public Completable updateCartItem(int productId, int updatedQuantity, int newStock) {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = null;
            try {

                db = databaseHelper.getWritableDatabase();
                db.beginTransaction();
                Log.d("CartDao", "updateCartItem: productId: " + productId + " updatedQuantity: " + updatedQuantity + " newStock: " + newStock);
                db.execSQL("UPDATE " + DatabaseHelper.TABLE_CART_ITEMS + " SET " + DatabaseHelper.COLUMN_QUANTITY_CART_ITEMS + " = " + updatedQuantity + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + " = " + productId);
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, newStock);
                db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                db.setTransactionSuccessful();

            } catch (Exception e) {
                throw new RuntimeException("Error updating cart item", e);
            }finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        });
    }

    @Override
    @SuppressLint("Range")
    public Completable clearCart() {
        return Completable.fromAction(() -> {
            SQLiteDatabase db = null;
            try {
                db = databaseHelper.getWritableDatabase();
                db.beginTransaction();
                SQLiteDatabase finalDb = db;

                // join cart items and products tables to get the stock of each product
                String query = "SELECT ci." + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + " AS cartProductId, " +
                        "ci." + DatabaseHelper.COLUMN_QUANTITY_CART_ITEMS + " AS cartProductQuantity, " +
                        "p." + DatabaseHelper.COLUMN_PRODUCT_QUANTITY + " AS productStock " +
                        "FROM " + DatabaseHelper.TABLE_CART_ITEMS + " ci " +
                        "JOIN " + DatabaseHelper.TABLE_PRODUCTS + " p " +
                        "ON ci." + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + " = p." + DatabaseHelper.COLUMN_PRODUCT_ID;

                Cursor cursor = finalDb.rawQuery(query, null);
                HashMap<Integer, Integer> stockMap = new HashMap<>();
                while (cursor.moveToNext()) {
                     int productId = cursor.getInt(cursor.getColumnIndex("cartProductId"));
                    int quantity = cursor.getInt(cursor.getColumnIndex("cartProductQuantity"));
                    int stock = cursor.getInt(cursor.getColumnIndex("productStock"));
                    Log.d("CartDao", "productId: " + productId + " quantity: " + quantity + " stock: " + stock);
                    stockMap.put(productId, stock + quantity);
                }
                cursor.close();

                stockMap.forEach((productId, stock) -> {
                    finalDb.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID_CART_ITEMS + " = " + productId);
                    ContentValues values = new ContentValues();
                    values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, stock);
                    finalDb.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                    // retrieve the stock of the product
                    Cursor productCursor = finalDb.rawQuery("SELECT " + DatabaseHelper.COLUMN_PRODUCT_QUANTITY + " FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                    productCursor.moveToFirst();
                    @SuppressLint("Range") int newStock = productCursor.getInt(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
                    productCursor.close();
                    Log.d("CartDao", "Stock after clearing cart: " + newStock);
                });
                db.setTransactionSuccessful();
            } catch (Exception e) {
                throw new RuntimeException("Error clearing cart", e);
            } finally {
                if (db != null) {
                    db.endTransaction();
                }
            }
        });
    }

    @Override
    @SuppressLint("Range")
    public Maybe<CartItem> getCartItem(int productId) {
        return Maybe.create(emitter -> {
            try {
                SQLiteDatabase db = databaseHelper.getReadableDatabase();
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
                    CartItem cartItem = new CartItem.Builder(productId)
                            .withCartItemId(_cartItemId)
                            .withQuantity(quantity)
                            .withPrice(price)
                            .withDiscount(discount)
                            .withProductName(productName)
                            .build();
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
