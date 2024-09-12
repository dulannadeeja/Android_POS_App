package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;

public interface ICustomerRepository {
    int newCustomerHandler(Customer customer);
    Customer getCustomerByIdHandler(int customerId);
    Single<ArrayList<Customer>> getAllCustomersHandler();
    void setCurrentCustomerHandler(int customerId);
    Customer getCurrentCustomerHandler();
    void clearCurrentCustomerHandler();
    double getCustomerOutstandingBalanceHandler(int customerId);
    void updateCustomerHandler(Customer customer);
}
