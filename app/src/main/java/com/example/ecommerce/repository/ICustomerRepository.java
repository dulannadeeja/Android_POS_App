package com.example.ecommerce.repository;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

public interface ICustomerRepository {
    int newCustomerHandler(Customer customer);
    Customer getCustomerByIdHandler(int customerId);
    ArrayList<Customer> getAllCustomersHandler();
    void setCurrentCustomerHandler(int customerId);
    Customer getCurrentCustomerHandler();
    void clearCurrentCustomerHandler();
    double getCustomerOutstandingBalanceHandler(int customerId);
}
