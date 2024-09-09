package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.App;
import com.example.ecommerce.dao.ICustomerDao;
import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

public class CustomerRepository implements ICustomerRepository {
    private ICustomerDao customerDao;
    private IOrderDao orderDao;
    private SharedPreferences sharedPreferences;

    public CustomerRepository(ICustomerDao customerDao, SharedPreferences sharedPreferences, IOrderDao orderDao) {
        this.customerDao = customerDao;
        this.sharedPreferences = sharedPreferences;
        this.orderDao = orderDao;
    }

    @Override
    public int newCustomerHandler(Customer customer) {
        try {
            return customerDao.createCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error creating customer", e);
        }
    }

    @Override
    public Customer getCustomerByIdHandler(int customerId) {
        try {
            return customerDao.getCustomerById(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting customer by id", e);
        }
    }

    @Override
    public ArrayList<Customer> getAllCustomersHandler() {
        try {
            return customerDao.getAllCustomers();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all customers", e);
        }
    }

    @Override
    public void setCurrentCustomerHandler(int customerId) {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("activeCustomerId", String.valueOf(customerId));
            editor.apply();
        } catch (Exception e) {
            throw new RuntimeException("Error saving current customer", e);
        }
    }

    @Override
    public Customer getCurrentCustomerHandler() {
        try {
            if (!sharedPreferences.contains("activeCustomerId")) return null;
            int customerId = Integer.parseInt(sharedPreferences.getString("activeCustomerId", ""));
            return customerDao.getCustomerById(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting current customer", e);
        }
    }

    @Override
    public void clearCurrentCustomerHandler() {
        try {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("activeCustomerId");
            editor.apply();
        } catch (Exception e) {
            throw new RuntimeException("Error clearing current customer", e);
        }
    }

    @Override
    public double getCustomerOutstandingBalanceHandler(int customerId) {
        try {
            Customer customer = customerDao.getCustomerById(customerId);
            if (customer == null || customer.getCustomerId() == 0) {
                throw new RuntimeException("Customer not found");
            }

            ArrayList<Order> orders = orderDao.getOrdersByCustomer(customerId);
            AtomicReference<Double> totalOutstandingBalance = new AtomicReference<>(0.0);
            if(orders != null && orders.size() > 0) {
                orders.forEach(order -> {
                    if (order.getDueAmount() > 0) {
                        totalOutstandingBalance.updateAndGet(v -> new Double((double) (v + order.getDueAmount())));
                    }
                });
            }
            return totalOutstandingBalance.get();
        } catch (Exception e) {
            throw new RuntimeException("Error getting current outstanding balance", e);
        }
    }
}
