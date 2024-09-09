package com.example.ecommerce.ui.customers.create_customer;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICustomerRepository;

public class CreateCustomerViewModelFactory implements ViewModelProvider.Factory {
    private ICustomerRepository customerRepository;

    public CreateCustomerViewModelFactory(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateCustomerViewModel.class)) {
            return (T) new CreateCustomerViewModel(customerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
