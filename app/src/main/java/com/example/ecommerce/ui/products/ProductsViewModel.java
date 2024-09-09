package com.example.ecommerce.ui.products;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.IProductRepository;

import java.util.ArrayList;

public class ProductsViewModel extends ViewModel {
    private final IProductRepository repository;
    private static final String TAG = "ProductsViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<ArrayList<Product>> products = new MutableLiveData<>();

    public ProductsViewModel(IProductRepository repository) {
        this.repository = repository;
    }

    public void setProducts() {
        try {
            isLoading.setValue(true);
            products.setValue(repository.getAllProducts());
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("UsersViewModel", "Error fetching products", e);
            errorMessage.setValue("Error fetching products");
        } finally {
            isLoading.setValue(false);
        }

    }

    public void onFilterProducts(String keyword){
        if(keyword.isEmpty()){
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
}