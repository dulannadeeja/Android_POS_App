package com.example.ecommerce.features.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IProductRepository;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final ICartRepository repository;
    private final IDiscountRepository discountRepository;
    private final IProductRepository productRepository;

    public CartViewModelFactory(ICartRepository repository, IDiscountRepository discountRepository, IProductRepository productRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(repository, discountRepository, productRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
