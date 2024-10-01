package com.example.ecommerce.model;

import java.util.ArrayList;
import java.util.Map;

public class Order {
    private int _orderId;
    private final String orderDate;
    private final double orderTotal;
    private final int discountId;
    private final double discountAmount;
    private final double taxAndCharges;
    private final double subTotal;
    private double paidAmount;
    private double dueAmount;
    private final int customerId;
    private String orderStatus;
    private final ArrayList<OrderItem> orderItems;

    private Order(OrderBuilder builder) {
        this._orderId = builder._orderId;
        this.orderDate = builder.orderDate;
        this.orderTotal = builder.orderTotal;
        this.discountId = builder.discountId;
        this.discountAmount = builder.discountAmount;
        this.taxAndCharges = builder.taxAndCharges;
        this.subTotal = builder.subTotal;
        this.paidAmount = builder.paidAmount;
        this.dueAmount = builder.dueAmount;
        this.customerId = builder.customerId;
        this.orderStatus = builder.orderStatus;
        this.orderItems = builder.orderItems;
    }

    public static class OrderBuilder {
        private int _orderId;
        private final String orderDate;
        private final double orderTotal;
        private int discountId;
        private double discountAmount;
        private double taxAndCharges;
        private double subTotal;
        private double paidAmount;
        private double dueAmount;
        private int customerId;
        private String orderStatus;
        private ArrayList<OrderItem> orderItems;

        public OrderBuilder(String orderDate, double orderTotal, String orderStatus, double taxAndCharges, double subTotal) {
            this.orderDate = orderDate;
            this.orderTotal = orderTotal;
            this.orderStatus = orderStatus;
            this.taxAndCharges = taxAndCharges;
            this.subTotal = subTotal;
        }

        public OrderBuilder withOrderId(int orderId) {
            this._orderId = orderId;
            return this;
        }

        public OrderBuilder withDiscount(int discountId, double discountAmount) {
            this.discountId = discountId;
            this.discountAmount = discountAmount;
            return this;
        }

        public OrderBuilder withPayment(double paidAmount, double dueAmount) {
            this.paidAmount = paidAmount;
            this.dueAmount = dueAmount;
            return this;
        }

        public OrderBuilder withCustomerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public OrderBuilder withOrderItems(ArrayList<OrderItem> orderItems) {
            this.orderItems = orderItems;
            return this;
        }

        public Order build() {
            return new Order(this);
        }

    }

    public void set_orderId(int _orderId) {
        this._orderId = _orderId;
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

    public int getDiscountId() {
        return discountId;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getTaxAndCharges() {
        return taxAndCharges;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setPaidAmount(double paidAmount) {
        this.paidAmount = paidAmount;
    }

    public double getPaidAmount() {
        return paidAmount;
    }

    public void setDueAmount(double dueAmount) {
        this.dueAmount = dueAmount;
    }

    public double getDueAmount() {
        return dueAmount;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public ArrayList<OrderItem> getOrderItems() {
        return orderItems;
    }
}
