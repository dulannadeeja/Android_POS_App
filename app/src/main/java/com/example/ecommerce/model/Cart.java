package com.example.ecommerce.model;

import java.util.ArrayList;

public class Cart implements Cloneable{
    private int totalItems;
    private double cartSubTotalPrice;
    private int discount_id;
    private double discount_value;
    private double cartTotalTax;
    private double cartTotalPrice;
    private ArrayList<CartItem> cartItems;

    public Cart(int totalItems, double cartSubTotalPrice, ArrayList<CartItem> cartItems, double cartTotalTax, int discount_id, double discount_value) {
        this.totalItems = totalItems;
        this.cartSubTotalPrice = cartSubTotalPrice;
        this.cartTotalTax = cartTotalTax;
        this.cartItems = cartItems;
        this.discount_id = discount_id;
        this.discount_value = discount_value;
        this.cartTotalPrice = cartSubTotalPrice + cartTotalTax - discount_value;
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

    public int getDiscountId() {
        return discount_id;
    }

    public double getDiscountValue() {
        return discount_value;
    }

    public void setTotalItems(int totalItems) {
        this.totalItems = totalItems;
    }

    public void setCartSubTotalPrice(double cartSubTotalPrice) {
        this.cartSubTotalPrice = cartSubTotalPrice;
    }

    public void setCartTotalTax(double cartTotalTax) {
        this.cartTotalTax = cartTotalTax;
    }

    public void setCartTotalPrice(double cartTotalPrice) {
        this.cartTotalPrice = cartTotalPrice;
    }

    public void setCartItems(ArrayList<CartItem> cartItems) {
        this.cartItems = cartItems;
    }

    public void setDiscountId(int discount_id) {
        this.discount_id = discount_id;
    }

    public void setDiscountValue(double discount_value) {
        this.discount_value = discount_value;
    }

    public void calculateCartTotalPrice() {
        double totalPrice = this.cartSubTotalPrice + this.cartTotalTax - this.discount_value;
        if(totalPrice < 0) {
            this.cartTotalPrice = 0;
        } else {
            this.cartTotalPrice = totalPrice;
        }
    }

    @Override
    public Cart clone() {
        try {
            Cart clone = (Cart) super.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
