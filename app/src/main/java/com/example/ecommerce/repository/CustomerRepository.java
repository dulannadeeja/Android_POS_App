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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.core.Single;

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
    public Single<Integer> newCustomerHandler(Customer customer) {
        try {
            return customerDao.createCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error creating customer", e);
        }
    }

    @Override
    public Completable updateCustomerHandler(Customer customer) {
        try {
            return customerDao.updateCustomer(customer);
        } catch (Exception e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }

    @Override
    public Single<Customer> getCustomerByIdHandler(int customerId) {
        try {
            return customerDao.getCustomerById(customerId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting customer by id", e);
        }
    }

    @Override
    public Single<ArrayList<Customer>> getAllCustomersHandler() {
        try {
            return customerDao.getAllCustomers();
        } catch (Exception e) {
            throw new RuntimeException("Error getting all customers", e);
        }
    }

    @Override
    public Completable setCurrentCustomerHandler(int customerId) {
        return Completable.fromAction(()->{
            try {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("activeCustomerId", String.valueOf(customerId));
                editor.apply();
            } catch (Exception e) {
                throw new RuntimeException("Error saving current customer", e);
            }
        });
    }

    @Override
    public Maybe<Customer> getCurrentCustomerHandler() {
        if (!sharedPreferences.contains("activeCustomerId")) {
            return Maybe.empty(); // Return empty Maybe if no customer ID exists.
        }
        try {
            int customerId = Integer.parseInt(sharedPreferences.getString("activeCustomerId", ""));
            return customerDao.getCustomerById(customerId)
                    .flatMapMaybe(customer -> {
                        if (customer == null || customer.getCustomerId() == 0) {
                            return Maybe.empty(); // Return empty Maybe if customer not found.
                        }
                        return Maybe.just(customer);
                    });
        } catch (Exception e) {
            return Maybe.error(new RuntimeException("Error getting current customer", e)); // Return Maybe.error on exception.
        }
    }

    @Override
    public Completable clearCurrentCustomerHandler() {
        return Completable.fromAction(() -> {
            try {
                Log.d("CustomerRepository", "Clearing current customer");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("activeCustomerId");
                editor.apply();
            } catch (Exception e) {
                Log.e("CustomerRepository", "Error clearing current customer", e);
                throw new RuntimeException("Error clearing current customer", e);
            }
        });
    }

    // TODO: Implement getCustomerOutstandingBalanceHandler
    @Override
    public double getCustomerOutstandingBalanceHandler(int customerId) {
        try {
//            Customer customer = customerDao.getCustomerById(customerId);
//            if (customer == null || customer.getCustomerId() == 0) {
//                throw new RuntimeException("Customer not found");
//            }
//
//            ArrayList<Order> orders = orderDao.getOrdersByCustomer(customerId);
            AtomicReference<Double> totalOutstandingBalance = new AtomicReference<>(0.0);
//            if(orders != null && orders.size() > 0) {
//                orders.forEach(order -> {
//                    if (order.getDueAmount() > 0) {
//                        totalOutstandingBalance.updateAndGet(v -> new Double((double) (v + order.getDueAmount())));
//                    }
//                });
//            }
            return totalOutstandingBalance.get();
        } catch (Exception e) {
            throw new RuntimeException("Error getting current outstanding balance", e);
        }
    }
}
