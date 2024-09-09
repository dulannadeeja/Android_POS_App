package com.example.ecommerce.model;

public class OpenOrderItem {
    private final String orderDate;
    private final Order order;

    public OpenOrderItem(String orderDate){
        this.orderDate = orderDate;
        this.order = null;
    }

    public OpenOrderItem(Order order){
        this.order = order;
        this.orderDate = null;
    }

    public boolean isDate(){
        return orderDate != null;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public Order getOrder() {
        return order;
    }
}
