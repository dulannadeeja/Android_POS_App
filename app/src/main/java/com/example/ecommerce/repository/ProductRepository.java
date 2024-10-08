package com.example.ecommerce.repository;

import android.util.Log;

import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.utils.RoomDBHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class ProductRepository implements IProductRepository {

    private static final String TAG = "ProductRepository";
    private final IProductDao productDao;

    public ProductRepository(RoomDBHelper database) {
        this.productDao = database.productDao();
    }

    @Override
    public Completable createProduct(Product product) {
        return productDao.createProduct(product);
    }

    @Override
    public Completable updateProduct(Product product) {
        return productDao.updateProduct(product);
    }

    @Override
    public Completable deleteProduct(Product product) {
        return productDao.deleteProduct(product);
    }

    @Override
    public Single<ArrayList<Product>> getAllProducts() {
        return productDao.getAllProducts()
                .map(ArrayList::new);
    }

    @Override
    public Single<Product> getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    @Override
    public Single<ArrayList<Product>> getFilteredProducts(String keyword) {
        return productDao.filterProducts(keyword)
                .map(ArrayList::new);
    }

    @Override
    public Single<Integer> getProductStock(int productId) {
        return productDao.getProductById(productId)
                .map(Product::getProductQuantity);
    }
}
