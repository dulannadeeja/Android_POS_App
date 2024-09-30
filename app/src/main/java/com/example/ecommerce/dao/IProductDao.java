package com.example.ecommerce.dao;

import com.example.ecommerce.model.Product;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface IProductDao {
    Completable createProduct(Product product);
    Completable updateProduct(Product product);
    Completable deleteProduct(Product product);
    Single<ArrayList<Product>> getAllProducts();
    Single<Product> getProductById(int productId);
    Single<ArrayList<Product>> filterProducts(String keyword);
    Single<Integer> getProductQuantity(int productId);
}
