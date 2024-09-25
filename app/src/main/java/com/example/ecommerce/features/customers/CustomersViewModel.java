package com.example.ecommerce.features.customers;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomersViewModel extends ViewModel {
    private final ICustomerRepository repository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<ArrayList<Customer>> customers = new MutableLiveData<>();

    public CustomersViewModel(ICustomerRepository repository) {
        this.repository = repository;
        this.onLoadCustomers();
    }

    public void onLoadCustomers() {
        Single<ArrayList<Customer>> customersSingle = repository.getAllCustomersHandler();
        compositeDisposable.add(customersSingle
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(cart -> {
                    customers.setValue(cart);
                }, throwable -> {
                    Toast.makeText(App.appModule.provideAppContext(), "Error loading customers", Toast.LENGTH_SHORT).show();
                }));
    }

    public MutableLiveData<ArrayList<Customer>> getCustomers() {
        return customers;
    }

    public void dispose() {
        compositeDisposable.dispose();
    }

    // Clear the compositeDisposable
    @Override
    public void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}