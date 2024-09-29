package com.example.ecommerce.features.products;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.utils.ProductsAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductsViewModel extends ViewModel {

    private final IProductRepository repository;
    private final ICustomerRepository customerRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private static final String TAG = "ProductsViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<ArrayList<Product>> products = new MutableLiveData<>();
    private static final MutableLiveData<Customer> currentCustomer = new MutableLiveData<>();
    private static final MutableLiveData<HashMap<Integer, Integer>> cartQuantityMap = new MutableLiveData<>(new HashMap<>());
    private static final MutableLiveData<HashMap<Integer, Integer>> productStockMap = new MutableLiveData<>(new HashMap<>());

    public ProductsViewModel(IProductRepository repository, ICustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public void setCustomer() {
        isLoading.setValue(true);
        compositeDisposable.add(
                customerRepository.getCurrentCustomerHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(customer -> {
                            currentCustomer.setValue(customer != null ? customer : new Customer.CustomerBuilder().buildCustomer());
                            errorMessage.setValue("");
                            isLoading.setValue(false);
                        }, throwable -> {
                            Log.e(TAG, "Error fetching customer", throwable);
                            errorMessage.setValue("Error fetching customer");
                            isLoading.setValue(false);
                        })
        );
    }

    public void setProducts() {
        try {
            isLoading.setValue(true);
            products.setValue(repository.getAllProducts());
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e(TAG, "Error fetching products", e);
            errorMessage.setValue("Error fetching products");
        } finally {
            isLoading.setValue(false);
        }

    }

    // This method updates the stock and quantity in the cart when items are added or removed.
    // It takes a list of updated cart items and modifies the product stock and cart quantities accordingly.
    public void onSetQuantityAndStock(ArrayList<CartItem> updatedCartItems, Boolean isOpenOrder) {

        // Create a set of product IDs in the updated cart for quick lookup
        Set<Integer> updatedCartProductIds = updatedCartItems.stream()
                .map(CartItem::getProductId)
                .collect(Collectors.toSet());

        productStockMap.getValue().clear();  // Clear the product stock map

        // Single loop to handle both removed items and updating stock/cart quantities
        cartQuantityMap.getValue().forEach((productId, cartQuantity) -> {
            Log.d(TAG, "cart quantity map item checked: " + productId);
            if (!updatedCartProductIds.contains(productId)) {
                Log.d(TAG, "Removed product found: " + productId);
                // Product is removed from the cart, restore stock
                productStockMap.getValue().put(productId, getProductStock(productId) + cartQuantity);  // Update stock
                cartQuantityMap.getValue().put(productId, 0);  // Reset cart quantity
            }
        });

        // Process updated cart items
        updatedCartItems.forEach(cartItem -> {

            Log.d(TAG, "Updated cart item: " + cartItem.getProductId());

            int productId = cartItem.getProductId();
            int newCartQuantity = cartItem.getQuantity();

            // Update cart quantity
            cartQuantityMap.getValue().put(productId, newCartQuantity);

            // Update product stock by subtracting the new cart quantity
            int updatedStock = getProductStock(productId) - newCartQuantity;
            productStockMap.getValue().put(productId, updatedStock);  // Save updated stock
        });
    }

    // Helper method to retrieve the product's current stock
    private int getProductStock(int productId) {
        return products.getValue().stream()
                .filter(product -> product.get_productId() == productId)
                .map(Product::getProductQuantity)
                .findFirst()
                .orElse(0);  // Return 0 if product not found
    }

    public void onFilterProducts(String keyword) {
        if (keyword.isEmpty()) {
            products.setValue(repository.getAllProducts());
        } else {
            products.setValue(repository.getFilteredProducts(keyword));
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<ArrayList<Product>> getProducts() {
        return products;
    }

    public MutableLiveData<Customer> getCurrentCustomer() {
        return currentCustomer;
    }

    public MutableLiveData<HashMap<Integer, Integer>> getCartQuantityMap() {
        return cartQuantityMap;
    }

    public MutableLiveData<HashMap<Integer, Integer>> getProductStockMap() {
        return productStockMap;
    }
}