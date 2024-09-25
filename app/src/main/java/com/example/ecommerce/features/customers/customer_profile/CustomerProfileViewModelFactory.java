package com.example.ecommerce.features.customers.customer_profile;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICustomerRepository;

public class CustomerProfileViewModelFactory implements ViewModelProvider.Factory {
    private final ICustomerRepository customerRepository;

    public CustomerProfileViewModelFactory(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CustomerProfileViewModel.class)) {
            return (T) new CustomerProfileViewModel(customerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
