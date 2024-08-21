package com.example.ecommerce.model;

public class CartItem {
    private final int _cartItemId;
    private final int productId;
    private int quantity;
    private double price;
    private String productName;
    private double discount;

    public CartItem(int _cartItemId, int productId, int quantity, double price, double discount, String productName) {
        this._cartItemId = _cartItemId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.productName = productName;
        this.discount = discount;
    }

    public int get_cartItemId() {
        return _cartItemId;
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

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public double getDiscount() {
        return discount;
    }
}
