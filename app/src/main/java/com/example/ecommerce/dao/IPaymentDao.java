package com.example.ecommerce.dao;

import com.example.ecommerce.model.Payment;

import java.util.ArrayList;

public interface IPaymentDao {
    void createPayment(Payment payment);
    ArrayList<Payment> filterPaymentsByOrder(int orderId);
}
