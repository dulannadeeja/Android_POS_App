package com.example.ecommerce.features.products;

import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IProductRepository;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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

    public ProductsViewModel(IProductRepository repository,ICustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    public void setCustomer(){
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