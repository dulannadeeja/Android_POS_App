package com.example.ecommerce.features.order;

public enum OrderStatus {
    PENDING("pending"),
    COMPLETED("completed"),
    CANCELLED("cancelled"),
    REFUNDED("refunded");

    private final String status;

    OrderStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}