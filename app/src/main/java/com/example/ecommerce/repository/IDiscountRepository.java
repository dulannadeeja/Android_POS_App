package com.example.ecommerce.repository;

import com.example.ecommerce.model.Discount;

import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public interface IDiscountRepository {
    int newDiscountHandler(Discount discount) throws Exception;
    void saveCurrentDiscount(Discount discount) throws Exception;
    Single<Discount> getCurrentDiscountHandler();
    void clearCurrentDiscount() throws Exception;
    Single<Map<String, Object>> applyCurrentDiscountHandler(double totalAmount);
    Discount getDiscountById(int discountId) throws Exception;
}
