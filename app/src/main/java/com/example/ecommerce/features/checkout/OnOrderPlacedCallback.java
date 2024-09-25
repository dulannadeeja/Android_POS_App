package com.example.ecommerce.features.checkout;

public interface OnOrderPlacedCallback {
    void onSuccessfulOrderPlaced();
    void onFailedOrderPlaced();
}
