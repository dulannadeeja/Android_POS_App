package com.example.ecommerce.dao;

import com.example.ecommerce.model.Product;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;

public interface IProductDao {
    void createProduct(Product product);
    void updateProduct(Product product);
    void deleteProduct(Product product);
    ArrayList<Product> getAllProducts();
    Single<Product> getProductById(int productId);
    ArrayList<Product> filterProducts(String keyword);
    int getProductQuantity(int productId);
}
