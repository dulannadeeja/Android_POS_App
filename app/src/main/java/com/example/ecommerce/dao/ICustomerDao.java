package com.example.ecommerce.dao;

import com.example.ecommerce.model.Customer;

import java.util.ArrayList;

public interface ICustomerDao {
    int createCustomer(Customer customer);
    Customer getCustomerById(int customerId);
    ArrayList<Customer> getAllCustomers();
}
