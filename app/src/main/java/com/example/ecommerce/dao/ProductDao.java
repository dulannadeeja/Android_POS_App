package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import kotlin.Suppress;

public class ProductDao implements IProductDao {

    private final DatabaseHelper databaseHelper;

    public ProductDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public void createProduct(Product product) {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {

            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProductName());
            values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.getProductDescription());
            values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.getProductPrice());
            values.put(DatabaseHelper.COLUMN_PRODUCT_COST, product.getProductCost());
            values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, product.getProductImage());
            values.put(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT, product.getProductDiscount());
            values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
            values.put(DatabaseHelper.COLUMN_PRODUCT_BRAND, product.getProductBrand());
            values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.getProductCategory());


            db.insertOrThrow(DatabaseHelper.TABLE_PRODUCTS, null, values);

        } catch (Exception e) {
            throw new RuntimeException("Error creating user", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProductName());
            values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.getProductDescription());
            values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.getProductPrice());
            values.put(DatabaseHelper.COLUMN_PRODUCT_COST, product.getProductCost());
            values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, product.getProductImage());
            values.put(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT, product.getProductDiscount());
            values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
            values.put(DatabaseHelper.COLUMN_PRODUCT_BRAND, product.getProductBrand());
            values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.getProductCategory());

            db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + product.get_productId(), null);
        } catch (Exception e) {
            throw new RuntimeException("Error updating product", e);
        }
    }

    @Override
    public void deleteProduct(Product product) {
        // TODO: Implement delete product method here
    }

    @SuppressLint("Range")
    @Override
    public ArrayList<Product> getAllProducts() {

        try(SQLiteDatabase db = databaseHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS, null)) {

            ArrayList<Product> products = new ArrayList<>();

            // loop through the cursor
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product.ProductBuilder()
                            .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
                            .withProductInfo(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY)))
                            .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
                            .withPricing(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
                                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE)))
                            .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
                            .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
                            .buildProduct();

                    products.add(product);
                } while (cursor.moveToNext());
                return products;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting products, From product dao", e);
        }
        return new ArrayList<>();
    }

    @SuppressLint("Range")
    @Override
    public Single<Product> getProductById(int productId) {
        return Single.create(
                emitter -> {
                    try {
                        SQLiteDatabase db = databaseHelper.getReadableDatabase();
                        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                        if (cursor.moveToFirst()) {
                            Product product = new Product.ProductBuilder()
                                    .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
                                    .withProductInfo(
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY))
                                    )
                                    .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
                                    .withPricing(
                                            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
                                            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
                                    )
                                    .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
                                    .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
                                    .buildProduct();
                            cursor.close();
                            emitter.onSuccess(product);
                        }
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
        );
    }

    @SuppressLint("Range")
    @Override
    public ArrayList<Product> filterProducts(String keyword) {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE '%" + keyword + "%'", null);
            ArrayList<Product> products = new ArrayList<>();
            if (cursor.moveToFirst()) {
                do {
                    Product product = new Product.ProductBuilder()
                            .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
                            .withProductInfo(
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
                                    cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY))
                            )
                            .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
                            .withPricing(
                                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
                                    cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
                            )
                            .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
                            .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
                            .buildProduct();
                    products.add(product);
                } while (cursor.moveToNext());
                cursor.close();
                return products;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting filtered products from product dao", e);
        }
        return new ArrayList<>();
    }

    @SuppressLint("Range")
    @Override
    public int getProductQuantity(int productId) {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_PRODUCT_QUANTITY + " FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
            if (cursor.moveToFirst()) {
                int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
                cursor.close();
                return quantity;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting product quantity from product dao", e);
        }
        return 0;
    }

    @Override
    public Completable updateProductQuantity(int productId, int quantity) {
        return Completable.create(
                emitter -> {
                    try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
                        ContentValues values = new ContentValues();
                        values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, quantity);
                        db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
                        emitter.onComplete();
                    } catch (Exception e) {
                        emitter.onError(e);
                    }
                }
        );
    }
}
