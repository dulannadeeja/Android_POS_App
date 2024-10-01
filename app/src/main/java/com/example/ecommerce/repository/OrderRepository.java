package com.example.ecommerce.repository;

import android.annotation.SuppressLint;

import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.features.order.OrderStatus;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.utils.DateHelper;

import java.util.ArrayList;
import java.util.NoSuchElementException;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderRepository implements IOrderRepository {
    private static final String TAG = "OrderRepository";
    private final IOrderDao orderDao;

    public OrderRepository(IOrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @SuppressLint("CheckResult")
    @Override
    public Single<Integer> createPendingOrderHandler(Order order) {
        return orderDao.createOrder(order)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io());
    }

    @Override
    public Completable updatePendingOrderHandler(Order order) {
        return orderDao.updateOrder(order);
    }

    @Override
    public Single<ArrayList<Order>> getPendingOrders() {
        return orderDao.filterOrdersByStatus(OrderStatus.PENDING.getStatus())
                .map(orders -> {
                    ArrayList<Order> reversedList = new ArrayList<>();
                    for (int i = orders.size() - 1; i >= 0; i--) {
                        reversedList.add(orders.get(i));
                    }
                    return reversedList;
                });
    }

    @Override
    public Single<ArrayList<OrderItem>> getOrderItems(int orderId) {
        return orderDao.getOrderItems(orderId);
    }

    @Override
    public Maybe<Order> getOrderById(int orderId) {
        return orderDao.getOrderById(orderId);
    }

    @Override
    public Single<Order> updateOrderPayment(double paymentAmount, int orderId) {
        return orderDao.getOrderById(orderId)
                .switchIfEmpty(Single.error(new NoSuchElementException("Order not found with id: " + orderId)))  // Convert Maybe to Single
                .flatMap(order -> {
                    double paidAmount = order.getPaidAmount() + paymentAmount;
                    double dueAmount = order.getDueAmount() - paymentAmount;
                    String orderStatus = dueAmount == 0 ? OrderStatus.COMPLETED.getStatus() : OrderStatus.PENDING.getStatus();

                    order.setPaidAmount(paidAmount);
                    order.setDueAmount(dueAmount);
                    order.setOrderStatus(orderStatus);

                    return orderDao.updateOrder(order)
                            .andThen(Single.just(order));  // Return Single<Order> after the update
                });
    }
}
