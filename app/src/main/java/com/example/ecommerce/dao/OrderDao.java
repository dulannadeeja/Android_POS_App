package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.ecommerce.features.order.OrderStatus;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.utils.DatabaseHelper;
import com.example.ecommerce.utils.DateHelper;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class OrderDao implements IOrderDao{
    private DatabaseHelper databaseHelper;

    public OrderDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public Single<Integer> createOrder(Order order) {
        return Single.fromCallable(() -> {
            SQLiteDatabase database = null;
            int createdOrderId = -1;

            try {

                // Get writable database
                database = databaseHelper.getWritableDatabase();

                // Begin the transaction
                database.beginTransaction();

                // Insert order values into the database
                ContentValues orderValues = new ContentValues();
                orderValues.put(DatabaseHelper.COLUMN_ORDER_DATE, order.getOrderDate());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_TOTAL, order.getOrderTotal());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_DISCOUNT_ID, order.getDiscountId());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_DISCOUNT_AMOUNT, order.getDiscountAmount());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_TAX_AND_CHARGES, order.getTaxAndCharges());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_SUB_TOTAL, order.getSubTotal());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT, order.getPaidAmount());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT, order.getDueAmount());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID, order.getCustomerId());
                orderValues.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getOrderStatus());

                // Insert the order into the database and get the new row ID
                createdOrderId = (int) database.insertOrThrow(DatabaseHelper.TABLE_ORDERS, null, orderValues);

                // Insert each order item related to the created order
                for (OrderItem orderItem : order.getOrderItems()) {
                    // Prepare values for the order item
                    ContentValues orderItemValues = new ContentValues();
                    orderItemValues.put(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS, createdOrderId); // Use the created order ID
                    orderItemValues.put(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS, orderItem.getProductId());
                    orderItemValues.put(DatabaseHelper.COLUMN_QUANTITY, orderItem.getQuantity());

                    // Insert the order item
                    database.insertOrThrow(DatabaseHelper.TABLE_ORDER_ITEMS, null, orderItemValues);
                }

                // Mark the transaction as successful
                database.setTransactionSuccessful();
            } catch (SQLiteException e) {
                throw new RuntimeException("Error creating order and its items", e);
            } finally {
                // End the transaction and close the database
                if (database != null) {
                    database.endTransaction();
                }
            }

            return createdOrderId;
        });
    }

    @SuppressLint("Range")
    @Override
    public Single<Order> getOrderById(int orderId) {
        return Single.create(
                emitter -> {
                    SQLiteDatabase database = databaseHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ID + " = " + orderId;
                    Cursor cursor = database.rawQuery(query, null);
                    Order order = null;
                    if(cursor.moveToFirst()) {
                        order = new Order.OrderBuilder(
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TOTAL)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TAX_AND_CHARGES)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_SUB_TOTAL)))
                                .withOrderId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID)))
                                .withDiscount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_ID)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_AMOUNT)))
                                .withPayment(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT)))
                                .withCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID)))
                                .build();
                    }
                    cursor.close();
                    emitter.onSuccess(order);
                }
        );
    }

    @SuppressLint("Range")
    @Override
    public Single<OrderItem> getOrderItemById(int orderItemId) {
        return Single.create(
                emitter -> {
                    SQLiteDatabase database = databaseHelper.getReadableDatabase();
                    String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ITEM_ID + " = " + orderItemId;
                    Cursor cursor = database.rawQuery(query, null);
                    OrderItem orderItem = null;
                    if(cursor.moveToFirst()) {
                        orderItem = new OrderItem(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY))
                        );
                        orderItem.set_orderItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ITEM_ID)));
                    }
                    cursor.close();
                    emitter.onSuccess(orderItem);
                }
        );
    }

    @SuppressLint("Range")
    @Override
    public Single<ArrayList<OrderItem>> getOrderItems(int orderId) {
        return Single.create(
                emitter -> {
                    SQLiteDatabase database = databaseHelper.getReadableDatabase();
                    ArrayList<OrderItem> orderItems = new ArrayList<>();
                    String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS + " = " + orderId;
                    Cursor cursor = database.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        OrderItem orderItem = new OrderItem(
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS)),
                                cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY))
                        );
                        orderItem.set_orderItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ITEM_ID)));
                        orderItems.add(orderItem);
                    }
                    cursor.close();
                    emitter.onSuccess(orderItems);
                }
        );
    }

    @Override
    @SuppressLint("Range")
    public Single<ArrayList<Order>> filterOrdersByStatus(String status){
        return Single.create(
                emitter -> {
                    SQLiteDatabase database = databaseHelper.getReadableDatabase();
                    ArrayList<Order> orders = new ArrayList<>();
                    String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE " + DatabaseHelper.COLUMN_ORDER_STATUS + " = '" + status + "'";
                    Cursor cursor = database.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        Order order = new Order.OrderBuilder(
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TOTAL)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TAX_AND_CHARGES)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_SUB_TOTAL)))
                                .withOrderId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID)))
                                .withDiscount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_ID)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_AMOUNT)))
                                .withPayment(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT)))
                                .withCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID)))
                                .build();
                        orders.add(order);
                    }
                    cursor.close();
                    emitter.onSuccess(orders);
                }
        );
    }

    @SuppressLint("Range")
    @Override
    public Single<ArrayList<Order>> getOrdersByCustomer(int customerId){
        return Single.create(
                emitter -> {
                    SQLiteDatabase database = databaseHelper.getReadableDatabase();
                    ArrayList<Order> orders = new ArrayList<>();
                    String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE " + DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID + " = " + customerId;
                    Cursor cursor = database.rawQuery(query, null);
                    while(cursor.moveToNext()){
                        Order order = new Order.OrderBuilder(
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DATE)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TOTAL)),
                                cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_STATUS)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_TAX_AND_CHARGES)),
                                cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_SUB_TOTAL)))
                                .withOrderId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID)))
                                .withDiscount(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_ID)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DISCOUNT_AMOUNT)))
                                .withPayment(cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT)), cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT)))
                                .withCustomerId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID)))
                                .build();
                        orders.add(order);
                    }
                    cursor.close();
                    emitter.onSuccess(orders);
                }
        );
    }

}
