package com.example.ecommerce.repository;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface IOrderRepository {
    Single<Integer> createPendingOrderHandler(Order order);
    Single<ArrayList<Order>> getPendingOrders();
    Single<ArrayList<OrderItem>> getOrderItems(int orderId);
    Completable updatePendingOrderHandler(Order order);
    Maybe<Order> getOrderById(int orderId);
    Single<Order> updateOrderPayment(double paymentAmount, int orderId);
}
