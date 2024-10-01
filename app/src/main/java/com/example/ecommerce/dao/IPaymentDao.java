package com.example.ecommerce.dao;

import com.example.ecommerce.model.Payment;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public interface IPaymentDao {
    Completable createPayment(Payment payment);
    Maybe<ArrayList<Payment>> filterPaymentsByOrder(int orderId);
}
