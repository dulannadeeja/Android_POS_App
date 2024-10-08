package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface IProductDao {
    @Insert
    Completable createProduct(Product product);
    @Update
    Completable updateProduct(Product product);
    @Delete
    Completable deleteProduct(Product product);
    @Query("SELECT * FROM products")
    Single<List<Product>> getAllProducts();
    @Query("SELECT * FROM products WHERE product_id = :productId")
    Single<Product> getProductById(int productId);
    @Query("SELECT * FROM products WHERE product_name LIKE '%' || :keyword || '%'")
    Single<List<Product>> filterProducts(String keyword);
    @Query("SELECT product_quantity FROM products WHERE product_id = :productId")
    Single<Integer> getProductQuantity(int productId);
}
