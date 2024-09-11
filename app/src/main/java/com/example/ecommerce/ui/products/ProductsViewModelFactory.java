package com.example.ecommerce.ui.products;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.repository.IProductRepository;

public class ProductsViewModelFactory implements ViewModelProvider.Factory {
    private final IProductRepository repository;
    private final ICustomerRepository customerRepository;

    public ProductsViewModelFactory(IProductRepository repository, ICustomerRepository customerRepository) {
        this.repository = repository;
        this.customerRepository = customerRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProductsViewModel.class)) {
            return (T) new ProductsViewModel(repository, customerRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
