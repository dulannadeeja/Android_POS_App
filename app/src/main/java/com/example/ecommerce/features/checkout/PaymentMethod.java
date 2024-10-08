package com.example.ecommerce.features.checkout;

public enum PaymentMethod {
    CARD("card"),
    CASH("cash"),
    CREDIT("credit"),
    CHECQUE("cheque");

    private final String method;

    PaymentMethod(String method) {
        this.method = method;
    }

    public String getMethod() {
        return method;
    }
}
