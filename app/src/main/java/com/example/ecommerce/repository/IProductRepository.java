package com.example.ecommerce.repository;

import com.example.ecommerce.model.Product;
import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;


public interface IProductRepository {
    void createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Product product);
    ArrayList<Product> getAllProducts();
    Single<Product> getProductById(int productId);
    ArrayList<Product> getFilteredProducts(String keyword);
    Completable reduceProductQuantity(int productId, int byCount);
    Completable increaseProductQuantity(int productId, int byCount);
    Single<Integer> getProductStock(int productId);
}
