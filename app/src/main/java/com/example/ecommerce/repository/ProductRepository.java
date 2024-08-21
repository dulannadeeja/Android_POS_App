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
        // TODO: Implement update product method here
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

    @Override
    public void addStaticData () {
        Product product1 = new Product(1, "Product 1", "Description 1", 100.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 10, "Brand 1", "Category 1");
        Product product2 = new Product(2, "Product 2", "Description 2", 200.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 20, "Brand 2", "Category 2");
        Product product3 = new Product(3, "Product 3", "Description 3", 300.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 30, "Brand 3", "Category 3");
        Product product4 = new Product(4, "Product 4", "Description 4", 400.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 40, "Brand 4", "Category 4");
        Product product5 = new Product(5, "Product 5", "Description 5", 500.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 50, "Brand 5", "Category 5");
        Product product6 = new Product(6, "Product 6", "Description 6", 600.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 60, "Brand 6", "Category 6");
        Product product7 = new Product(7, "Product 7", "Description 7", 700.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 70, "Brand 7", "Category 7");
        Product product8 = new Product(8, "Product 8", "Description 8", 800.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 80, "Brand 8", "Category 8");
        Product product9 = new Product(9, "Product 9", "Description 9", 900.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 90, "Brand 9", "Category 9");
        Product product10 = new Product(10, "Product 10", "Description 10", 1000.0, "https://www.apple.com/newsroom/images/product/iphone/geo/Apple_iphone13_hero_geo_09142021_inline.jpg.large_2x.jpg", (double) 0, 100, "Brand 10", "Category 10");

        createProduct(product1);
        createProduct(product2);
        createProduct(product3);
        createProduct(product4);
        createProduct(product5);
        createProduct(product6);
        createProduct(product7);
        createProduct(product8);
        createProduct(product9);
        createProduct(product10);
    }
}
