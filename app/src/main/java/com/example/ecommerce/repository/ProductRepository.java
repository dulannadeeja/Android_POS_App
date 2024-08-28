package com.example.ecommerce.repository;

import android.util.Log;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProductRepository implements IProductRepository {

    private final IProductDao productDao;

    public ProductRepository(IProductDao productDao) {
        this.productDao = productDao;
    }

    @Override
    public void createProduct(Product product) {
        try{
            productDao.createProduct(product);
        } catch (Exception e) {
            Log.e("ProductRepository", "Error creating product", e);
        }
    }

    @Override
    public void updateProduct(Product product) {
        try{
            productDao.updateProduct(product);
        } catch (Exception e) {
            Log.e("ProductRepository", "Error updating product", e);
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
            Log.e("ProductRepository", "Error getting all products", e);
            return new ArrayList<>();
        }
    }

    @Override
    public Product getProductById(int productId) {
        try {
            return productDao.getProductById(productId);
        } catch (Exception e) {
            Log.e("ProductRepository", "Error getting product by id", e);
            return null;
        }
    }

    @Override
    public ArrayList<Product> getFilteredProducts(String keyword) {
        try {
            return productDao.filterProducts(keyword);
        } catch (Exception e) {
            Log.e("ProductRepository", "Error getting filtered products", e);
            return new ArrayList<>();
        }
    }
}
