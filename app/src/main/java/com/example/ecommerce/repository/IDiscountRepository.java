package com.example.ecommerce.repository;

import com.example.ecommerce.model.Discount;

import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public interface IDiscountRepository {
    Single<Integer> newDiscountHandler(Discount discount);
    Completable saveCurrentDiscount(Discount discount);
    Single<Discount> getCurrentDiscountHandler();
    Completable clearCurrentDiscount();
    Single<Map<String, Object>> applyCurrentDiscountHandler(double totalAmount);
    Maybe<Discount> getDiscountById(int discountId);
}
