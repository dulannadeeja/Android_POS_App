package com.example.ecommerce.dao;

import com.example.ecommerce.model.Discount;

import java.util.ArrayList;
import java.util.Date;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface IDiscountDao {
    Single<Integer> createDiscount(Discount discount) ;
    Maybe<Discount> getDiscount(int discountId) ;
    Maybe<Discount> isExistingDiscount(String discountType, double discountValue);
}
