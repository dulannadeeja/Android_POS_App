package com.example.ecommerce.ui.cart;

public interface OnCartOperationCompleted {
    void onSuccessfulCartOperation();
    void onFailedCartOperation(String message);
}
