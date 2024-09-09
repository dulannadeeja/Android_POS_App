package com.example.ecommerce.model;

public class Payment {
    private int _paymentId;
    private final String paymentMethod;
    private final double paymentAmount;
    private final String paymentDate;
    private final int orderId;

    public Payment( String paymentMethod, double paymentAmount, String paymentDate, int orderId) {
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.orderId = orderId;
    }

    public void setPaymentId(int paymentId) {
        this._paymentId = paymentId;
    }

    public int getPaymentId() {
        return _paymentId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public int getOrderId() {
        return orderId;
    }

}
