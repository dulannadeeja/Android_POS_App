package com.example.ecommerce.features.customers;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomerViewModel extends AndroidViewModel {
    private static final String TAG = "CustomerViewModel";
    private ICustomerRepository customerRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Customer> customer = new MutableLiveData<>(new Customer.CustomerBuilder().buildCustomer());


    public CustomerViewModel(@NonNull Application application, ICustomerRepository customerRepository) {
        super(application);
        this.customerRepository = customerRepository;
    }

    public void onLoadCustomer(int customerId) {
        compositeDisposable.add(
                customerRepository.getCustomerByIdHandler(customerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(customer -> {
                            this.customer.setValue(customer);
                        }, throwable -> {
                            Log.e(TAG, "error while fetching customer, ", throwable);
                        })
        );
    }

    public void onSetCurrentCustomer(int customerId) {
        compositeDisposable.add(
                customerRepository.setCurrentCustomerHandler(customerId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onLoadCurrentCustomer, throwable -> {
                            Log.e(TAG, "error while setting current customer, ", throwable);
                        })
        );
    }

    public void onClearCurrentCustomer() {
        compositeDisposable.add(
                customerRepository.clearCurrentCustomerHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(this::onLoadCurrentCustomer, throwable -> {
                            Log.e(TAG, "error while clearing current customer, ", throwable);
                        })
        );
    }

    public void onLoadCurrentCustomer() {
        compositeDisposable.add(
                customerRepository.getCurrentCustomerHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(customer -> {
                            this.customer.setValue(customer);
                        }, throwable -> {
                            Log.e(TAG, "error while fetching current customer, ", throwable);
                        }, () -> {
                            this.customer.setValue(new Customer.CustomerBuilder().buildCustomer());
                        })
        );
    }

    public <T> void applyUpdateToCustomer(String field, T value) {
        Customer customerClone = customer.getValue();
        switch (field) {
            case "firstName":
                customerClone.setFirstName((String) value);
                break;
            case "lastName":
                customerClone.setLastName((String) value);
                break;
            case "email":
                customerClone.setEmail((String) value);
                break;
            case "phone":
                customerClone.setPhone((String) value);
                break;
            case "address":
                customerClone.setAddress((String) value);
                break;
            case "city":
                customerClone.setCity((String) value);
                break;
            case "region":
                customerClone.setRegion((String) value);
                break;
            case "gender":
                customerClone.setGender((String) value);
                break;
            case "photo":
                customerClone.setPhoto((String) value);
                break;
            default:
                throw new IllegalArgumentException("Invalid field name" + field);

        }

        customer.setValue(customerClone);
    }

    public void onClearCustomer() {
        customer.setValue(new Customer.CustomerBuilder().buildCustomer());
    }

    public MutableLiveData<Customer> getCustomer() {
        return customer;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}
