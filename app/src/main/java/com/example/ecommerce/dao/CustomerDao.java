package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomerDao implements ICustomerDao {
    private DatabaseHelper databaseHelper;

    public CustomerDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int createCustomer(Customer customer) {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CUSTOMER_FIRST_NAME, customer.getFirstName());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_LAST_NAME, customer.getLastName());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_EMAIL, customer.getEmail());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_PHONE, customer.getPhone());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS, customer.getAddress());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_CITY, customer.getCity());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_REGION, customer.getRegion());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_GENDER, customer.getGender());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_PHOTO, customer.getPhoto());
            return (int) db.insertOrThrow(DatabaseHelper.TABLE_CUSTOMERS, null, values);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error creating customer", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public Customer getCustomerById(int customerId) {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_CUSTOMERS + " WHERE " + DatabaseHelper.COLUMN_CUSTOMER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(customerId)};
            Cursor cursor = db.rawQuery(query, selectionArgs);
            Customer customer = null;
            if (cursor.moveToFirst()) {
                customer = new Customer.CustomerBuilder()
                        .withCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ID)))
                        .withName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_FIRST_NAME)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_LAST_NAME)))
                        .withEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_EMAIL)))
                        .withPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_PHONE)))
                        .withAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS)),cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_CITY)),cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_REGION)))
                        .withGender(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_GENDER)))
                        .withPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_PHOTO)))
                        .buildCustomer();
            }
            cursor.close();
            return customer;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting customer by id", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public Single<ArrayList<Customer>> getAllCustomers() {
        return Single.<ArrayList<Customer>>create(emitter -> {
                    SQLiteDatabase db = null;
                    Cursor cursor = null;
                    try {
                        db = databaseHelper.getReadableDatabase();
                        String query = "SELECT * FROM " + DatabaseHelper.TABLE_CUSTOMERS + " ORDER BY " + DatabaseHelper.COLUMN_CUSTOMER_ID + " DESC";
                        cursor = db.rawQuery(query, null);
                        ArrayList<Customer> customers = new ArrayList<>();

                        while (cursor.moveToNext()) {
                            Customer customer = new Customer.CustomerBuilder()
                                    .withCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ID)))
                                    .withName(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_FIRST_NAME)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_LAST_NAME)))
                                    .withEmail(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_EMAIL)))
                                    .withPhone(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_PHONE)))
                                    .withAddress(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_CITY)),
                                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_REGION)))
                                    .withGender(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_GENDER)))
                                    .withPhoto(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CUSTOMER_PHOTO)))
                                    .buildCustomer();
                            customers.add(customer);
                        }
                        emitter.onSuccess(customers);
                    } catch (SQLiteException e) {
                        emitter.onError(e);
                    } finally {
                        if (cursor != null && !cursor.isClosed()) {
                            cursor.close();
                        }
                        if (db != null && db.isOpen()) {
                            db.close();
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }


    @Override
    public void updateCustomer(Customer customer) {
        try(SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_CUSTOMER_FIRST_NAME, customer.getFirstName());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_LAST_NAME, customer.getLastName());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_EMAIL, customer.getEmail());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_PHONE, customer.getPhone());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_ADDRESS, customer.getAddress());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_CITY, customer.getCity());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_REGION, customer.getRegion());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_GENDER, customer.getGender());
            values.put(DatabaseHelper.COLUMN_CUSTOMER_PHOTO, customer.getPhoto());
            String selection = DatabaseHelper.COLUMN_CUSTOMER_ID + " = ?";
            String[] selectionArgs = {String.valueOf(customer.getCustomerId())};
            db.update(DatabaseHelper.TABLE_CUSTOMERS, values, selection, selectionArgs);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error updating customer", e);
        }
    }
}
