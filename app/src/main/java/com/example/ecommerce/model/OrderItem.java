package com.example.ecommerce.model;

public class OrderItem {
    private final int _orderItemId;
    private final int orderId;
    private final int productId;
    private int quantity;

    public OrderItem(int orderItemId, int orderId, int productId, int quantity) {
        this._orderItemId = orderItemId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public int get_orderItemId() {
        return _orderItemId;
    }

    public int getOrderId() {
        return orderId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
