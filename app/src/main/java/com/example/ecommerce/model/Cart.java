package com.example.ecommerce.model;

import java.util.ArrayList;

public class Cart {
    private int totalItems;
    private double cartSubTotalPrice;
    private double cartTotalTax;
    private double cartTotalPrice;
    private ArrayList<CartItem> cartItems;

    public Cart(int totalItems, double cartSubTotalPrice, ArrayList<CartItem> cartItems, double cartTotalTax) {
        this.totalItems = totalItems;
        this.cartSubTotalPrice = cartSubTotalPrice;
        this.cartTotalTax = cartTotalTax;
        this.cartTotalPrice = cartSubTotalPrice + cartTotalTax;
        this.cartItems = cartItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    public double getCartSubTotalPrice() {
        return cartSubTotalPrice;
    }

    public double getCartTotalTax() {
        return cartTotalTax;
    }

    public double getCartTotalPrice() {
        return cartTotalPrice;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

}
