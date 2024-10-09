package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.dao.ICustomerDao;
import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.utils.RoomDBHelper;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Repository class responsible for handling data operations related to customers.
 * This class interacts with the DAO layer for database operations and uses SharedPreferences
 * for storing and retrieving customer session-related data.
 */
public class CustomerRepository implements ICustomerRepository {

    private final ICustomerDao customerDao;
    private final IOrderDao orderDao;
    private final SharedPreferences sharedPreferences;

    /**
     * Constructor for CustomerRepository.
     *
     * @param database          Room database helper to access DAOs.
     * @param sharedPreferences SharedPreferences instance for storing active customer data.
     */
    public CustomerRepository(RoomDBHelper database, SharedPreferences sharedPreferences) {
        this.customerDao = database.customerDao();
        this.sharedPreferences = sharedPreferences;
        this.orderDao = database.orderDao();
    }

    /**
     * Inserts a new customer into the database.
     *
     * @param customer The customer to be created.
     * @return A Single emitting the newly created customer ID.
     */
    @Override
    public Single<Integer> newCustomerHandler(Customer customer) {
        return customerDao.createCustomer(customer)
                .map(customerId -> {
                    Log.d("CustomerRepository", "New customer created with ID: " + customerId);
                    return customerId.intValue();
                })
                .onErrorResumeNext(throwable -> {
                    Log.e("CustomerRepository", "Error creating customer", throwable);
                    throw new RuntimeException("Error creating customer", throwable);
                });
    }

    /**
     * Updates an existing customer's details in the database.
     *
     * @param customer The customer entity with updated details.
     * @return A Completable signaling completion.
     */
    @Override
    public Completable updateCustomerHandler(Customer customer) {
        return customerDao.updateCustomer(customer)
                .onErrorResumeNext(throwable -> {
                    Log.e("CustomerRepository", "Error updating customer", throwable);
                    throw new RuntimeException("Error updating customer", throwable);
                });
    }

    /**
     * Retrieves a customer by their ID.
     *
     * @param customerId The customer ID.
     * @return A Single emitting the corresponding customer.
     */
    @Override
    public Single<Customer> getCustomerByIdHandler(int customerId) {
        return customerDao.getCustomerById(customerId)
                .onErrorResumeNext(throwable -> {
                    Log.e("CustomerRepository", "Error retrieving customer by ID", throwable);
                    throw new RuntimeException("Error retrieving customer by ID", throwable);
                });
    }

    /**
     * Retrieves all customers from the database.
     *
     * @return A Single emitting a list of customers.
     */
    @Override
    public Single<ArrayList<Customer>> getAllCustomersHandler() {
        return customerDao.getAllCustomers()
                .map(customers -> customers != null ? new ArrayList<Customer>(customers) : new ArrayList<Customer>())
                .onErrorResumeNext(throwable -> {
                    Log.e("CustomerRepository", "Error retrieving all customers", throwable);
                    throw new RuntimeException("Error retrieving all customers", throwable);
                });
    }

    /**
     * Sets the current customer in SharedPreferences by their ID.
     *
     * @param customerId The ID of the active customer.
     * @return A Completable signaling completion.
     */
    @Override
    public Completable setCurrentCustomerHandler(int customerId) {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("activeCustomerId", String.valueOf(customerId));
            editor.apply();
        }).onErrorResumeNext(throwable -> {
            Log.e("CustomerRepository", "Error setting current customer", throwable);
            throw new RuntimeException("Error setting current customer", throwable);
        });
    }

    /**
     * Retrieves the current active customer from SharedPreferences.
     *
     * @return A Maybe emitting the active customer or empty if no active customer is set.
     */
    @Override
    public Maybe<Customer> getCurrentCustomerHandler() {
        if (!sharedPreferences.contains("activeCustomerId")) {
            return Maybe.empty(); // No active customer found
        }

        try {
            int customerId = Integer.parseInt(sharedPreferences.getString("activeCustomerId", ""));
            return customerDao.getCustomerById(customerId)
                    .flatMapMaybe(customer -> customer != null ? Maybe.just(customer) : Maybe.empty());
        } catch (Exception e) {
            return Maybe.error(new RuntimeException("Error retrieving current customer", e));
        }
    }

    /**
     * Clears the current active customer from SharedPreferences.
     *
     * @return A Completable signaling completion.
     */
    @Override
    public Completable clearCurrentCustomerHandler() {
        return Completable.fromAction(() -> {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove("activeCustomerId");
            editor.apply();
            Log.d("CustomerRepository", "Cleared current customer");
        }).onErrorResumeNext(throwable -> {
            Log.e("CustomerRepository", "Error clearing current customer", throwable);
            throw new RuntimeException("Error clearing current customer", throwable);
        });
    }

    /**
     * Retrieves the outstanding balance of a customer by their ID based on their orders.
     *
     * @param customerId The ID of the customer.
     * @return A Single emitting the total outstanding balance.
     */
    @Override
    public Single<Double> getCustomerOutstandingBalanceHandler(int customerId) {
        return customerDao.getCustomerById(customerId)
                .flatMap(customer -> {
                    if (customer == null || customer.getCustomerId() == 0) {
                        return Single.error(new RuntimeException("Customer not found"));
                    }

                    return orderDao.getOrdersByCustomer(customerId)
                            .map(orders -> {
                                AtomicReference<Double> totalOutstandingBalance = new AtomicReference<>(0.0);
                                if (orders != null && !orders.isEmpty()) {
                                    orders.forEach(order -> {
                                        if (order.getDueAmount() > 0) {
                                            totalOutstandingBalance.updateAndGet(balance -> balance + order.getDueAmount());
                                        }
                                    });
                                }
                                return totalOutstandingBalance.get();
                            });
                })
                .onErrorResumeNext(throwable -> {
                    Log.e("CustomerRepository", "Error retrieving customer outstanding balance", throwable);
                    throw new RuntimeException("Error retrieving customer outstanding balance", throwable);
                });
    }
}
