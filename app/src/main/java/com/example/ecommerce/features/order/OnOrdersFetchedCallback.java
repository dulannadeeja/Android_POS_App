package com.example.ecommerce.features.order;

import com.example.ecommerce.model.Order;

import java.util.ArrayList;

public interface OnOrdersFetchedCallback {
    void onOrdersFetched(ArrayList<Order> orders);
    void onOrdersFetchFailed(String errorMessage);
}
