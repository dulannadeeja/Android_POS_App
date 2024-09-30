package com.example.ecommerce.features.products;

import android.util.Log;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IProductRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
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

    public void setProducts() {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.getAllProducts()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(fetchedProducts -> {
                            products.setValue(fetchedProducts);
                            isLoading.setValue(false);
                        }, throwable -> {
                            Log.e(TAG, "Error fetching products", throwable);
                            errorMessage.setValue("Error fetching products");
                            isLoading.setValue(false);
                        })
        );
    }

    public void onChangeCart(Cart cart, OnCartChangesAppliedCallback callback) {
        ArrayList<CartItem> updatedCartItems = cart.getCartItems();
        ArrayList<Integer> productIdsToUpdate = new ArrayList<>();

        // Add all the product ids to the list from the current cart
        Objects.requireNonNull(cartQuantityMap.getValue()).forEach((productId, cartQuantity) -> {
            productIdsToUpdate.add(productId);
            // Add product IDs with 0 quantity to the cart quantity map
            if (updatedCartItems.stream().noneMatch(cartItem -> cartItem.getProductId() == productId)) {
                cartQuantityMap.getValue().put(productId, 0);
            }
        });

        // Update the cart quantities and ensure all relevant product IDs are collected
        updatedCartItems.forEach(cartItem -> {
            int productId = cartItem.getProductId();
            int cartQuantity = cartItem.getQuantity();
            if (!productIdsToUpdate.contains(productId)) {
                productIdsToUpdate.add(productId);
            }
            cartQuantityMap.getValue().put(productId, cartQuantity);
        });

        // Clear the current stock map to update with fresh data
        productStockMap.getValue().clear();

        // Prepare a list of Singles to fetch stock for each product
        List<Single<Pair<Integer, Integer>>> stockFetches = new ArrayList<>();
        productIdsToUpdate.forEach(productId -> {
            Single<Pair<Integer, Integer>> stockSingle = repository.getProductStock(productId)
                    .subscribeOn(Schedulers.io())
                    .map(stock -> new Pair<>(productId, stock))
                    .onErrorReturnItem(new Pair<>(productId, 0))
                    .doOnError(throwable -> {
                        Log.e(TAG, "Error fetching product stock", throwable);
                        errorMessage.setValue("Error fetching product stock");
                    });
            stockFetches.add(stockSingle);
        });

        compositeDisposable.add(
                // Use Single.zip to wait for all product stock fetches to complete
                Single.zip(stockFetches, objects -> {
                            // Map the results back to productStockMap
                            for (Object obj : objects) {
                                Pair<Integer, Integer> productStock = (Pair<Integer, Integer>) obj;
                                productStockMap.getValue().put(productStock.first, productStock.second);
                            }
                            return true; // Just returning a placeholder value
                        }).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(success -> {
                            // Invoke the callback after the stock has been updated
                            callback.onSuccessfulCartChanges(cartQuantityMap.getValue(), productStockMap.getValue(), productIdsToUpdate);
                        }, throwable -> {
                            Log.e(TAG, "Error applying cart changes", throwable);
                            callback.onFailedCartChanges(throwable.getMessage());
                        })
        );
    }

    public void onFilterProducts(String keyword) {
        if (keyword.isEmpty()) {
            setProducts();
        } else {
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.getFilteredProducts(keyword)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(fetchedProducts -> {
                                        products.setValue(fetchedProducts);
                                        isLoading.setValue(false);
                                    },
                                    throwable -> {
                                        Log.e(TAG, "Error fetching filtered products", throwable);
                                        errorMessage.setValue("Error fetching filtered products");
                                        isLoading.setValue(false);
                                    }

                            ));
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
}