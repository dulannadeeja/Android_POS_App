package com.example.ecommerce.ui.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICartRepository;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final ICartRepository repository;

    public CartViewModelFactory(ICartRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
