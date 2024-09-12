package com.example.ecommerce.dao;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Single;

public interface ICustomerDao {
    int createCustomer(Customer customer);
    Customer getCustomerById(int customerId);
    Single<ArrayList<Customer>> getAllCustomers();
    void updateCustomer(Customer customer);
}
