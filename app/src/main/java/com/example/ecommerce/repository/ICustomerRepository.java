package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface ICustomerRepository {
    Single<Integer> newCustomerHandler(Customer customer);
    Single<Customer> getCustomerByIdHandler(int customerId);
    Single<ArrayList<Customer>> getAllCustomersHandler();
    Completable setCurrentCustomerHandler(int customerId);
    Maybe<Customer> getCurrentCustomerHandler();
    Completable clearCurrentCustomerHandler();
    Single<Double> getCustomerOutstandingBalanceHandler(int customerId);
    Completable updateCustomerHandler(Customer customer);
}
