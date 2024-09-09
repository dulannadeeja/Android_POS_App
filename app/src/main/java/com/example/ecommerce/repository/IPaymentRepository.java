package com.example.ecommerce.repository;

public interface IPaymentRepository {
    void handlePayment(String paymentMethod, double paymentAmount, int orderId);
}
