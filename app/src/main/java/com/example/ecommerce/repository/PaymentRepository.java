package com.example.ecommerce.repository;

import com.example.ecommerce.dao.IPaymentDao;
import com.example.ecommerce.model.Payment;
import com.example.ecommerce.utils.DateHelper;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public class PaymentRepository implements IPaymentRepository {
    private static final String TAG = "PaymentRepository";
    private final IPaymentDao paymentDao;

    public PaymentRepository(IPaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public Completable paymentHandler(String paymentMethod, double paymentAmount, int orderId) {
        String date = DateHelper.getTimeStamp();
        Payment newPayment = new Payment.Builder()
                .withPaymentMethod(paymentMethod)
                .withPaymentAmount(paymentAmount)
                .withPaymentDate(date)
                .withOrderId(orderId)
                .build();
        return paymentDao.createPayment(newPayment);
    }

    @Override
    public Maybe<ArrayList<Payment>> getPaymentsByOrder(int orderId) {
        return paymentDao.filterPaymentsByOrder(orderId);
    }
}
