package com.example.ecommerce.repository;

public interface IApplyDiscountCallback {
    void onDiscountApplied(double discountAmount, int discountId);
    void onDiscountError(String error);
}
