package com.example.ecommerce.features.cart;

public interface OnSavedPendingOrderCallback {
    void onSuccessfulOrderSaved();
    void onFailedOrderSaved(String message);
}
