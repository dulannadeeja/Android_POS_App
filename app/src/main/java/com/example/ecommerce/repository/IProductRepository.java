package com.example.ecommerce.repository;

import androidx.room.Dao;

import com.example.ecommerce.model.Product;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface IProductRepository {
    Completable createProduct(Product product);
    Completable updateProduct(Product product);
    Completable deleteProduct(Product product);
    Single<ArrayList<Product>> getAllProducts();
    Single<Product> getProductById(int productId);
    Single<ArrayList<Product>> getFilteredProducts(String keyword);
    Single<Integer> getProductStock(int productId);
}
