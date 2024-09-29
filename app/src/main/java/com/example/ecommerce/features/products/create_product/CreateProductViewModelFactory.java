package com.example.ecommerce.features.products.create_product;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.IProductRepository;

public class CreateProductViewModelFactory implements ViewModelProvider.Factory {
    private final IProductRepository repository;
    private final Product product;

    public CreateProductViewModelFactory(IProductRepository repository, Product product) {
        this.repository = repository;
        this.product = product;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CreateProductViewModel.class)) {
            return (T) new CreateProductViewModel(repository, product);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
