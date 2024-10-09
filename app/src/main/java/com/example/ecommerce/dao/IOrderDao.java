package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface IOrderDao {
    @Insert
    Single<Long> createOrder(Order order);
    @Query("SELECT * FROM orders WHERE order_id = :orderId")
    Maybe<Order> getOrderById(int orderId);
    @Query("SELECT * FROM order_items WHERE order_item_id = :orderItemId")
    Single<OrderItem> getOrderItemById(int orderItemId);
    @Query("SELECT * FROM orders WHERE order_status = :status")
    Single<List<Order>> filterOrdersByStatus(String status);
    @Query("SELECT * FROM order_items WHERE order_id = :orderId")
    Single<List<OrderItem>> getOrderItems(int orderId);
    @Query("SELECT * FROM orders WHERE customer_id = :customerId")
    Single<List<Order>> getOrdersByCustomer(int customerId);
    @Update
    Completable updateOrder(Order order);
}
