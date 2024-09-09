package com.example.ecommerce.dao;

import com.example.ecommerce.model.Discount;

import java.util.ArrayList;
import java.util.Date;

public interface IDiscountDao {
    int createDiscount(Discount discount) throws Exception;
    Discount getDiscount(int discountId) throws Exception;
    Discount isExistingDiscount(String discountType, double discountValue) throws Exception;
}
