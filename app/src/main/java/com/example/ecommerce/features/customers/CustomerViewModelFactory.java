package com.example.ecommerce.features.customers;

import android.app.Application;

import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICustomerRepository;

public class CustomerViewModelFactory implements ViewModelProvider.Factory {
    private final ICustomerRepository repository;
    private final Application app;

    public CustomerViewModelFactory(Application app,ICustomerRepository repository) {
        this.repository = repository;
        this.app = app;
    }

    @Override
    public <T extends androidx.lifecycle.ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CustomerViewModel.class)) {
            return (T) new CustomerViewModel(app,repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
