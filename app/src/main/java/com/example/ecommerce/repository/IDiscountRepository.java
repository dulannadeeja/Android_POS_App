package com.example.ecommerce.repository;

import com.example.ecommerce.model.Discount;

public interface IDiscountRepository {
    void addNewDiscount(Discount discount) throws Exception;
    Discount getCurrentDiscount() throws Exception;
    void clearDiscount() throws Exception;
    double getDiscountAmount(double totalAmount) throws Exception;
}
