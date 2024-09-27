package com.example.ecommerce.dao;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface ICustomerDao {
    Single<Integer> createCustomer(Customer customer);
    Single<Customer> getCustomerById(int customerId);
    Single<ArrayList<Customer>> getAllCustomers();
    Completable updateCustomer(Customer customer);
}
