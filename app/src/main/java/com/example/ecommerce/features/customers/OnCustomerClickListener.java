package com.example.ecommerce.features.customers;

import com.example.ecommerce.model.Customer;

public interface OnCustomerClickListener {
    void onAddCustomerClicked(Customer customer);
    void onCustomerClick(Customer customer);
}
