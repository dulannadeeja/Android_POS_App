package com.example.ecommerce.repository;

import android.util.Log;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class ProductRepository implements IProductRepository {

    private static final String TAG = "ProductRepository";
    private final IProductDao productDao;

    public ProductRepository(IProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void createProduct(Product product) {
        try{
            productDao.createProduct(product);
        } catch (Exception e) {
            Log.e(TAG, "Error creating product", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try{
            productDao.updateProduct(product);
        } catch (Exception e) {
            Log.e(TAG, "Error updating product", e);
        }
    }

    @Override
    public void deleteProduct(Product product) {
        // TODO: Implement delete product method here
    }

    @Override
    public ArrayList<Product> getAllProducts() {
        try {
            return productDao.getAllProducts();
        } catch (Exception e) {
            Log.e(TAG, "Error getting all products", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Single<Product> getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    @Override
    public ArrayList<Product> getFilteredProducts(String keyword) {
        try {
            return productDao.filterProducts(keyword);
        } catch (Exception e) {
            Log.e(TAG, "Error getting filtered products", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Completable reduceProductQuantity(int productId, int byCount) {
        return productDao.getProductById(productId)
                .flatMapCompletable(product -> {
                    if(product.getProductQuantity() - byCount >= 0) {
                        return productDao.updateProductQuantity(productId, product.getProductQuantity() - byCount);
                    } else {
                        return Completable.error(new RuntimeException("Product out of stock"));
                    }
                });
    }

    @Override
    public Completable increaseProductQuantity(int productId, int byCount) {
        return productDao.getProductById(productId)
                .flatMapCompletable(product -> productDao.updateProductQuantity(productId, product.getProductQuantity() + byCount));
    }

    @Override
    public Single<Integer> getProductStock(int productId) {
        return productDao.getProductById(productId)
                .map(Product::getProductQuantity);
    }
}
