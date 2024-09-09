package com.example.ecommerce.model;

public class OrderItem {
    private int _orderItemId;
    private final int orderId;
    private final int productId;
    private final int quantity;

    public OrderItem(int orderId, int productId, int quantity) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
    }

    public void set_orderItemId(int _orderItemId) {
        this._orderItemId = _orderItemId;
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
}
