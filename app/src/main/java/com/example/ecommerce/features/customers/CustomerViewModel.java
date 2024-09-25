package com.example.ecommerce.features.customers;

import android.app.Application;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

public class CustomerViewModel extends AndroidViewModel {
    private static final String TAG = "CustomerViewModel";
    private ICustomerRepository customerRepository;

    private MutableLiveData<Customer> customer = new MutableLiveData<>(new Customer.CustomerBuilder().buildCustomer());


    public CustomerViewModel(@NonNull Application application, ICustomerRepository customerRepository) {
        super(application);
        this.customerRepository = customerRepository;
    }

    public void onLoadCustomer(int customerId) {
        try{
            Customer customer = customerRepository.getCustomerByIdHandler(customerId);
            this.customer.setValue(customer);
        }catch (Exception e){
            Log.e(TAG, "error while fetching customer, ", e);
        }
    }

    public void onSetCurrentCustomer(int customerId) {
        try{
            customerRepository.setCurrentCustomerHandler(customerId);
            onLoadCurrentCustomer();
        }catch (Exception e){
            Log.e(TAG, "error while setting current customer, ", e);
        }
    }

    public void onClearCurrentCustomer() {
        try{
            customerRepository.clearCurrentCustomerHandler();
            onLoadCurrentCustomer();
        }catch (Exception e){
            Log.e(TAG, "error while clearing current customer, ", e);
        }
    }

    public void onLoadCurrentCustomer() {
        try{
            Customer currentCustomer = customerRepository.getCurrentCustomerHandler();
            currentCustomer = currentCustomer != null ? currentCustomer : new Customer.CustomerBuilder().buildCustomer();
            customer.setValue(currentCustomer);
        }catch (Exception e){
            Log.e(TAG, "error while fetching current customer, ", e);
        }
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
}
