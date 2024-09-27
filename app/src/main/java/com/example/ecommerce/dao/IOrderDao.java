package com.example.ecommerce.dao;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface IOrderDao {
    Single<Integer> createOrder(Order order);
    Single<Order> getOrderById(int orderId);
    Single<OrderItem> getOrderItemById(int orderItemId);
    Single<ArrayList<Order>> filterOrdersByStatus(String status);
    Single<ArrayList<OrderItem>> getOrderItems(int orderId);
    Single<ArrayList<Order>> getOrdersByCustomer(int customerId);
    Completable updateOrder(Order order);
}
