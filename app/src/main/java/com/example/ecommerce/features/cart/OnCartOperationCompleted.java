package com.example.ecommerce.features.cart;

public interface OnCartOperationCompleted {
    void onSuccessfulCartOperation();
    void onFailedCartOperation(String message);
}
