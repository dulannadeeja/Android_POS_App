package com.example.ecommerce.dao;

import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;

public interface IOrderDao {
    long createOrder(Order order);
    Order getOrderById(int orderId);
    void createOrderItem(OrderItem orderItem);
    OrderItem getOrderItemById(int orderItemId);
    void updateOrderPayment(int orderId, double paidAmount, double dueAmount, String orderStatus);
    ArrayList<Order> filterOrdersByStatus(String status);
    ArrayList<OrderItem> getOrderItems(int orderId);
    ArrayList<Order> getOrdersByCustomer(int customerId);
}
