package com.example.ecommerce.model;

import androidx.annotation.NonNull;

import java.util.ArrayList;

public class Cart implements Cloneable{
    private double cartSubTotalPrice;
    private double cartTotalTaxAndCharges;
    private double cartTotalPrice;
    private Boolean isOpenOrder;
    private int orderId;
    private Boolean isDiscountApplied;
    private int discountId;
    private double discountValue;
    private Boolean isCartEmpty;
    private ArrayList<CartItem> cartItems;
    private int totalItems;

    public Cart(double cartSubTotalPrice,double cartTotalTaxAndCharges,int orderId,int discountId,double discountValue, ArrayList<CartItem> cartItems) {
        this.cartSubTotalPrice = cartSubTotalPrice;
        this.cartTotalTaxAndCharges = cartTotalTaxAndCharges;
        this.cartTotalPrice = calculateCartTotalPrice(discountId > 0, discountValue, cartSubTotalPrice, cartTotalTaxAndCharges);
        this.isOpenOrder = orderId > 0;
        this.orderId = orderId;
        this.isDiscountApplied = discountId > 0 && discountValue > 0;
        this.discountId = discountId;
        this.discountValue = discountValue;
        this.isCartEmpty = cartItems.isEmpty();
        this.cartItems = cartItems;
        this.totalItems = getCartTotalItems(cartItems);
    }

    private int getCartTotalItems(ArrayList<CartItem> cartItems) {
        if(cartItems == null || cartItems.isEmpty()){
            return 0;
        }
        return cartItems.stream().mapToInt(CartItem::getQuantity).sum();
    }

    private double calculateCartTotalPrice(boolean isDiscountApplied, double discountValue, double cartSubTotalPrice, double cartTotalTax) {
        double cartTotalPrice = cartSubTotalPrice + cartTotalTax - (isDiscountApplied ? discountValue : 0);
        return Math.max(cartTotalPrice, 0);
    }

    public static class CartBuilder {
        private double cartSubTotalPrice = 0;
        private double cartTotalTaxAndCharges = 0;
        private int orderId = -1;
        private int discountId = -1;
        private double discountValue = 0;
        private ArrayList<CartItem> cartItems = new ArrayList<>();

        public CartBuilder withCartSubTotalPrice(double cartSubTotalPrice){
            this.cartSubTotalPrice = cartSubTotalPrice;
            return this;
        }

        public CartBuilder withCartTotalTaxAndCharges(double cartTotalTaxAndCharges){
            this.cartTotalTaxAndCharges = cartTotalTaxAndCharges;
            return this;
        }

        public CartBuilder withOrderId(int orderId){
            this.orderId = orderId;
            return this;
        }

        public CartBuilder withDiscount(int discountId, double discountValue){
            this.discountId = discountId;
            this.discountValue = discountValue;
            return this;
        }

        public CartBuilder withCartItems(ArrayList<CartItem> cartItems){
            this.cartItems = cartItems;
            return this;
        }

        public Cart build(){
            return new Cart(cartSubTotalPrice,cartTotalTaxAndCharges,orderId,discountId,discountValue,cartItems);
        }
    }

    public double getCartSubTotalPrice() {
        return cartSubTotalPrice;
    }

    public double getCartTotalTaxAndCharges() {
        return cartTotalTaxAndCharges;
    }

    public double getCartTotalPrice() {
        return cartTotalPrice;
    }

    public Boolean isOpenOrder() {
        return isOpenOrder;
    }

    public int getOrderId() {
        return orderId;
    }

    public Boolean isDiscountApplied() {
        return isDiscountApplied;
    }

    public int getDiscountId() {
        return discountId;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public Boolean isCartEmpty() {
        return isCartEmpty;
    }

    public ArrayList<CartItem> getCartItems() {
        return cartItems;
    }

    public int getTotalItems() {
        return totalItems;
    }

    @NonNull
    @Override
    public Cart clone() {
        try {
            return (Cart) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
