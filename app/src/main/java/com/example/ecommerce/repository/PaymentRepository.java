package com.example.ecommerce.repository;

import com.example.ecommerce.dao.IPaymentDao;
import com.example.ecommerce.model.Payment;

public class PaymentRepository implements IPaymentRepository {
    private static final String TAG = "PaymentRepository";
    private final IPaymentDao paymentDao;

    public PaymentRepository(IPaymentDao paymentDao) {
        this.paymentDao = paymentDao;
    }

    @Override
    public void handlePayment(String paymentMethod, double paymentAmount, int orderId) {
        String date = (new java.util.Date()).toString();
        Payment newPayment = new Payment(paymentMethod, paymentAmount, date, orderId);
        paymentDao.createPayment(newPayment);
    }
}
