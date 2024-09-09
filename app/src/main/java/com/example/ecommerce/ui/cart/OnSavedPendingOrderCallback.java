package com.example.ecommerce.ui.cart;

public interface OnSavedPendingOrderCallback {
    void onSuccessfulOrderSaved();
    void onFailedOrderSaved(String message);
}
