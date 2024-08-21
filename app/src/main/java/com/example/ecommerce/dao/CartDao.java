package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;

public class CartDao implements ICartDao {

    private final DatabaseHelper databaseHelper;

    public CartDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @SuppressLint("Range")
    @Override
    public ArrayList<CartItem> getCartItems() throws Exception {
        try(SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CART_ITEMS, null);
            ArrayList<CartItem> cartItems = new ArrayList<>();
            while (cursor.moveToNext()) {
                int _cartItemId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CART_ITEM_ID));
                int productId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID));
                int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY));

                Cursor productCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                productCursor.moveToFirst();
                double price = productCursor.getDouble(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE));
                double discount = productCursor.getDouble(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT));
                String productName = productCursor.getString(productCursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME));
                productCursor.close();
                CartItem cartItem = new CartItem(_cartItemId, productId, quantity, price, discount, productName);
                cartItems.add(cartItem);
            }
            cursor.close();
            return cartItems;
        } catch (Exception e) {
            throw new RuntimeException("Error getting cart items", e);
        }
    }

    @Override
    public void createCartItem(int productId) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.execSQL("INSERT INTO " + DatabaseHelper.TABLE_CART_ITEMS + " (" + DatabaseHelper.COLUMN_PRODUCT_ID + ", " + DatabaseHelper.COLUMN_QUANTITY + ") VALUES (" + productId + ", 1)");
        } catch (Exception e) {
            throw new RuntimeException("Error creating cart item", e);
        }
    }

    @Override
    public void deleteCartItem(int productId) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting cart item", e);
        }
    }

    @Override
    public void updateCartItem(int productId, int quantity) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.execSQL("UPDATE " + DatabaseHelper.TABLE_CART_ITEMS + " SET " + DatabaseHelper.COLUMN_QUANTITY + " = " + quantity + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId);
        } catch (Exception e) {
            throw new RuntimeException("Error updating cart item", e);
        }
    }

    @Override
    public void clearCart() throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            db.execSQL("DELETE FROM " + DatabaseHelper.TABLE_CART_ITEMS);
        } catch (Exception e) {
            throw new RuntimeException("Error clearing cart", e);
        }
    }

}
