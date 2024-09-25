package com.example.ecommerce.features.discount;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.IDiscountRepository;

public class DiscountViewModelFactory implements ViewModelProvider.Factory {
    private final IDiscountRepository repository;

    public DiscountViewModelFactory(IDiscountRepository repository) {
        this.repository = repository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DiscountViewModel.class)) {
            return (T) new DiscountViewModel(repository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
