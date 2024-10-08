//package com.example.ecommerce.dao;
//
//import android.annotation.SuppressLint;
//import android.content.ContentValues;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import com.example.ecommerce.model.Product;
//import com.example.ecommerce.utils.DatabaseHelper;
//import java.util.ArrayList;
//import io.reactivex.rxjava3.core.Completable;
//import io.reactivex.rxjava3.core.Single;
//
//public class ProductDao implements IProductDao {
//
//    private final DatabaseHelper databaseHelper;
//
//    public ProductDao(DatabaseHelper databaseHelper) {
//        this.databaseHelper = databaseHelper;
//    }
//
//    @Override
//    public Completable createProduct(Product product) {
//        return Completable.create(
//                emitter -> {
//                    try {
//                        SQLiteDatabase db = databaseHelper.getWritableDatabase();
//                        ContentValues values = new ContentValues();
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProductName());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.getProductDescription());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.getProductPrice());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_COST, product.getProductCost());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, product.getProductImage());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT, product.getProductDiscount());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_BRAND, product.getProductBrand());
//                        values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.getProductCategory());
//
//                        db.insertOrThrow(DatabaseHelper.TABLE_PRODUCTS, null, values);
//                        emitter.onComplete();
//                    } catch (Exception e) {
//                        emitter.onError(e);
//                    }
//                }
//        );
//    }
//
//    @Override
//    public Completable updateProduct(Product product) {
//        return Completable.create(emitter -> {
//            SQLiteDatabase db = null;
//            try {
//                db = databaseHelper.getWritableDatabase();
//                ContentValues values = new ContentValues();
//                values.put(DatabaseHelper.COLUMN_PRODUCT_NAME, product.getProductName());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION, product.getProductDescription());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_PRICE, product.getProductPrice());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_COST, product.getProductCost());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_IMAGE, product.getProductImage());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT, product.getProductDiscount());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_QUANTITY, product.getProductQuantity());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_BRAND, product.getProductBrand());
//                values.put(DatabaseHelper.COLUMN_PRODUCT_CATEGORY, product.getProductCategory());
//
//                db.update(DatabaseHelper.TABLE_PRODUCTS, values, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + product.get_productId(), null);
//                emitter.onComplete();
//            } catch (Exception e) {
//                throw new RuntimeException("Error updating product", e);
//            } finally {
//                if (db != null) {
//                    db.close();
//                }
//                emitter.onComplete();
//            }
//        });
//    }
//
//    @Override
//    public Completable deleteProduct(Product product) {
//        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
//            db.delete(DatabaseHelper.TABLE_PRODUCTS, DatabaseHelper.COLUMN_PRODUCT_ID + " = " + product.get_productId(), null);
//            return Completable.complete();
//        } catch (Exception e) {
//            return Completable.error(e);
//        }
//    }
//
//    @SuppressLint("Range")
//    @Override
//    public Single<ArrayList<Product>> getAllProducts() {
//        return Single.create(emitter -> {
//            try {
//                SQLiteDatabase db = databaseHelper.getReadableDatabase();
//                Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS, null);
//                ArrayList<Product> products = new ArrayList<>();
//                if (cursor.moveToFirst()) {
//                    do {
//                        Product product = new Product.ProductBuilder()
//                                .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
//                                .withProductInfo(
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY))
//                                )
//                                .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
//                                .withPricing(
//                                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
//                                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
//                                )
//                                .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
//                                .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
//                                .buildProduct();
//                        products.add(product);
//                    } while (cursor.moveToNext());
//                    cursor.close();
//                    emitter.onSuccess(products);
//                }
//            } catch (Exception e) {
//                emitter.onError(e);
//            }
//        });
//    }
//
//    @SuppressLint("Range")
//    @Override
//    public Single<Product> getProductById(int productId) {
//        return Single.create(
//                emitter -> {
//                    try {
//                        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//                        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
//                        if (cursor.moveToFirst()) {
//                            Product product = new Product.ProductBuilder()
//                                    .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
//                                    .withProductInfo(
//                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
//                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
//                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
//                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY))
//                                    )
//                                    .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
//                                    .withPricing(
//                                            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
//                                            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
//                                    )
//                                    .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
//                                    .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
//                                    .buildProduct();
//                            cursor.close();
//                            emitter.onSuccess(product);
//                        }
//                    } catch (Exception e) {
//                        emitter.onError(e);
//                    }
//                }
//        );
//    }
//
//    @SuppressLint("Range")
//    @Override
//    public Single<ArrayList<Product>> filterProducts(String keyword) {
//        return Single.create(emitter -> {
//            try {
//                SQLiteDatabase db = databaseHelper.getReadableDatabase();
//                Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_NAME + " LIKE '%" + keyword + "%'", null);
//                ArrayList<Product> products = new ArrayList<>();
//                if (cursor.moveToFirst()) {
//                    do {
//                        Product product = new Product.ProductBuilder()
//                                .withProductId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID)))
//                                .withProductInfo(
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_NAME)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DESCRIPTION)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_BRAND)),
//                                        cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_CATEGORY))
//                                )
//                                .withDiscount(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_DISCOUNT)))
//                                .withPricing(
//                                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_COST)),
//                                        cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_PRICE))
//                                )
//                                .withQuantity(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY)))
//                                .withImage(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_IMAGE)))
//                                .buildProduct();
//                        products.add(product);
//                    } while (cursor.moveToNext());
//                    cursor.close();
//                    emitter.onSuccess(products);
//                }
//            } catch (Exception e) {
//                emitter.onError(e);
//            }
//        });
//    }
//
//    @SuppressLint("Range")
//    @Override
//    public Single<Integer> getProductQuantity(int productId) {
//        return Single.create(
//                emitter -> {
//                    try {
//                        SQLiteDatabase db = databaseHelper.getReadableDatabase();
//                        Cursor cursor = db.rawQuery("SELECT " + DatabaseHelper.COLUMN_PRODUCT_QUANTITY + " FROM " + DatabaseHelper.TABLE_PRODUCTS + " WHERE " + DatabaseHelper.COLUMN_PRODUCT_ID + " = " + productId, null);
//                        if (cursor.moveToFirst()) {
//                            int quantity = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_QUANTITY));
//                            cursor.close();
//                            emitter.onSuccess(quantity);
//                        }
//                    } catch (Exception e) {
//                        emitter.onError(e);
//                    }
//                }
//        );
//    }
//}
