package com.example.ecommerce.features.customers;

import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICustomerRepository;

public class CustomersViewModelFactory implements ViewModelProvider.Factory {
    private final ICustomerRepository repository;

    public CustomersViewModelFactory(ICustomerRepository repository) {
        this.repository = repository;
    }

    @Override
    public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CustomersViewModel.class)) {
            return (T) new CustomersViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
