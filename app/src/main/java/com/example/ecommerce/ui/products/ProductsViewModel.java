package com.example.ecommerce.ui.products;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IProductRepository;

import java.util.ArrayList;

public class ProductsViewModel extends ViewModel {

    private final IProductRepository repository;
    private final ICustomerRepository customerRepository;

    private static final String TAG = "ProductsViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<ArrayList<Product>> products = new MutableLiveData<>();

    private static final MutableLiveData<Customer> currentCustomer = new MutableLiveData<>();

    public ProductsViewModel(IProductRepository repository,ICustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public void setCustomer(){
        Customer customer = customerRepository.getCurrentCustomerHandler();
        currentCustomer.setValue(customer != null ? customer : new Customer.CustomerBuilder().buildCustomer());
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

    public MutableLiveData<Customer> getCurrentCustomer() {
        return currentCustomer;
    }
}