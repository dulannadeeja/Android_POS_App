package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;

public class OrderDao implements IOrderDao{
    private DatabaseHelper databaseHelper;

    public OrderDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public long createOrder(Order order) {
        long newRowId = -1;
        try (SQLiteDatabase database = databaseHelper.getWritableDatabase()) {
            // Create order
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_DATE, order.getOrderDate());
            values.put(DatabaseHelper.COLUMN_ORDER_TOTAL, order.getOrderTotal());
            values.put(DatabaseHelper.COLUMN_ORDER_DISCOUNT_ID, order.getDiscountId());
            values.put(DatabaseHelper.COLUMN_ORDER_DISCOUNT_AMOUNT, order.getDiscountAmount());
            values.put(DatabaseHelper.COLUMN_ORDER_TAX_AND_CHARGES, order.getTaxAndCharges());
            values.put(DatabaseHelper.COLUMN_ORDER_SUB_TOTAL, order.getSubTotal());
            values.put(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT, order.getPaidAmount());
            values.put(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT, order.getDueAmount());
            values.put(DatabaseHelper.COLUMN_ORDER_CUSTOMER_ID, order.getCustomerId());
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, order.getOrderStatus());

            // Insert the order into the database and get the new row ID
            newRowId = database.insertOrThrow(DatabaseHelper.TABLE_ORDERS, null, values);

        } catch (SQLiteException e) {
            throw new RuntimeException("Error creating order", e);
        }

        return newRowId;
    }

    @SuppressLint("Range")
    @Override
    public Order getOrderById(int orderId) {
        try(SQLiteDatabase database = databaseHelper.getReadableDatabase()) {
            // Get order
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDERS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ID + " = " + orderId;
            Order order = null;
            Cursor cursor = database.rawQuery(query, null);
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
            return order;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting order", e);
        }
    }

    @Override
    public void createOrderItem(OrderItem orderItem) {
        try(SQLiteDatabase database = databaseHelper.getWritableDatabase()) {
            // Create order item
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS, orderItem.getOrderId());
            values.put(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS, orderItem.getProductId());
            values.put(DatabaseHelper.COLUMN_QUANTITY, orderItem.getQuantity());
            database.insertOrThrow(DatabaseHelper.TABLE_ORDER_ITEMS, null, values);
        } catch (SQLiteException e) {
            throw new RuntimeException("Error creating order item", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public OrderItem getOrderItemById(int orderItemId) {
        try(SQLiteDatabase database = databaseHelper.getReadableDatabase()) {
            // Get order item
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ITEM_ID + " = " + orderItemId;
            OrderItem orderItem = null;
            Cursor cursor = database.rawQuery(query, null);
            if(cursor.moveToFirst()) {
                orderItem = new OrderItem(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY))
                );
                orderItem.set_orderItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ITEM_ID)));
            }
            cursor.close();
            return orderItem;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting order item", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public ArrayList<OrderItem> getOrderItems(int orderId) {
        try(SQLiteDatabase database = databaseHelper.getReadableDatabase()) {
            // Get order items
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_ORDER_ITEMS + " WHERE " + DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS + " = " + orderId;
            ArrayList<OrderItem> orderItems = new ArrayList<>();
            Cursor cursor = database.rawQuery(query, null);
            while(cursor.moveToNext()) {
                 OrderItem orderItem = new OrderItem(
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ID_ORDER_ITEMS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PRODUCT_ID_ORDER_ITEMS)),
                        cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_QUANTITY))
                );
                orderItem.set_orderItemId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ORDER_ITEM_ID)));
                orderItems.add(orderItem);
            }
            cursor.close();
            return orderItems;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting order items", e);
        }
    }

    @Override
    public void updateOrderPayment(int orderId, double paidAmount, double dueAmount, String orderStatus) {
        try(SQLiteDatabase database = databaseHelper.getWritableDatabase()) {
            ContentValues values = new ContentValues();
            values.put(DatabaseHelper.COLUMN_ORDER_PAID_AMOUNT, paidAmount);
            values.put(DatabaseHelper.COLUMN_ORDER_DUE_AMOUNT, dueAmount);
            values.put(DatabaseHelper.COLUMN_ORDER_STATUS, orderStatus);
            database.update(DatabaseHelper.TABLE_ORDERS, values, DatabaseHelper.COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order payment", e);
        }
    }

    @Override
    @SuppressLint("Range")
    public ArrayList<Order> filterOrdersByStatus(String status){
        try{
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
            return orders;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting orders", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public ArrayList<Order> getOrdersByCustomer(int customerId){
        try{
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
            return orders;
        } catch (SQLiteException e) {
            throw new RuntimeException("Error getting orders", e);
        }
    }

}
