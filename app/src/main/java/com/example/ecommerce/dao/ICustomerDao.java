package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.model.Customer;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

/**
 * Data Access Object (DAO) interface for handling customer-related database operations.
 * This interface interacts with the `customers` table in the database.
 *
 * Provides methods for creating, updating, retrieving, and querying customers.
 */
@Dao
public interface ICustomerDao {

    /**
     * Inserts a new customer into the `customers` table.
     *
     * @param customer The Customer object to be inserted.
     * @return Single emitting the newly inserted customer ID as Long upon success.
     */
    @Insert
    Single<Long> createCustomer(Customer customer);

    /**
     * Retrieves a customer by its unique customer ID from the `customers` table.
     *
     * @param customerId The unique ID of the customer to be retrieved.
     * @return Single emitting the Customer object upon successful retrieval.
     */
    @Query("SELECT * FROM customers WHERE customer_id = :customerId")
    Single<Customer> getCustomerById(int customerId);

    /**
     * Retrieves all customers from the `customers` table.
     *
     * @return Single emitting a list of all Customer objects stored in the database.
     */
    @Query("SELECT * FROM customers")
    Single<List<Customer>> getAllCustomers();

    /**
     * Updates an existing customer record in the `customers` table.
     *
     * @param customer The Customer object with updated fields.
     * @return Completable indicating the success or failure of the update operation.
     */
    @Update
    Completable updateCustomer(Customer customer);
}
