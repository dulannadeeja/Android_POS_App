package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ecommerce.model.Payment;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface IPaymentDao {
    @Insert
    Single<Long> createPayment(Payment payment);
    @Query("SELECT * FROM payments WHERE order_id = :orderId")
    Maybe<List<Payment>> filterPaymentsByOrder(int orderId);
}
