package com.example.ecommerce.repository;

import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;

public interface IOrderRepository {
    long handleNewOrder(ArrayList<CartItem> cartItems,double orderTotal, double taxAndCharges, double subTotal, int discountId, double discountAmount, int customerId);
    void makeOrderPayment(double paymentAmount, int orderId);
    ArrayList<Order> getPendingOrders();
    ArrayList<OrderItem> getOrderItems(int orderId);
}
