package com.example.ecommerce.repository;

import com.example.ecommerce.model.Payment;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public interface IPaymentRepository {
    Completable paymentHandler(String paymentMethod, double paymentAmount, int orderId);
    Maybe<ArrayList<Payment>> getPaymentsByOrder(int orderId);
}
