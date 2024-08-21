package com.example.ecommerce.model;

public class Order {
    private final int _orderId;
    private final String orderDate;
    private final double orderTotal;
    private final String orderStatus;

    public Order(int orderId, String orderDate, double orderTotal, String orderStatus) {
        this._orderId = orderId;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.orderStatus = orderStatus;
    }

    public int get_orderId() {
        return _orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public double getOrderTotal() {
        return orderTotal;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

}
