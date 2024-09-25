package com.example.ecommerce.model;

public class CartItem {
    private final Integer _cartItemId; // Optional, can be null
    private final int productId;
    private int quantity;
    private double price;
    private String productName;
    private double discount;

    // Private constructor to prevent direct instantiation
    private CartItem(Builder builder) {
        this._cartItemId = builder._cartItemId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.productName = builder.productName;
        this.discount = builder.discount;
    }

    // Getters
    public Integer getCartItemId() {
        return _cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
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

    // Setters for mutable fields
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Static Builder class
    public static class Builder {
        private Integer _cartItemId = null;  // Optional field, default is null
        private final int productId;
        private int quantity;
        private double price;
        private String productName;
        private double discount;

        // Constructor for mandatory fields
        public Builder(int productId) {
            this.productId = productId;
        }

        // Setter method for optional cartItemId with new naming convention
        public Builder withCartItemId(int cartItemId) {
            this._cartItemId = cartItemId;
            return this;
        }

        // Setter methods for optional fields in the builder
        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder withDiscount(double discount) {
            this.discount = discount;
            return this;
        }

        // Build method to create a CartItem instance
        public CartItem build() {
            return new CartItem(this);
        }
    }
}
