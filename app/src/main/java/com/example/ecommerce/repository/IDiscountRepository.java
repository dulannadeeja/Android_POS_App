package com.example.ecommerce.repository;

import com.example.ecommerce.model.Discount;

public interface IDiscountRepository {
    int newDiscountHandler(Discount discount) throws Exception;

    void saveCurrentDiscount(Discount discount) throws Exception;

    Discount getCurrentDiscount() throws Exception;

    void clearCurrentDiscount() throws Exception;

    double getDiscountAmount(double totalAmount, IApplyDiscountCallback callback) throws Exception;

    Discount getDiscountById(int discountId) throws Exception;
}
