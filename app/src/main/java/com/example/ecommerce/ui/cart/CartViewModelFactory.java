package com.example.ecommerce.ui.cart;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IOrderRepository;

public class CartViewModelFactory implements ViewModelProvider.Factory {
    private final ICartRepository repository;
    private final IDiscountRepository discountRepository;
    private final IOrderRepository orderRepository;

    public CartViewModelFactory(ICartRepository repository, IDiscountRepository discountRepository, IOrderRepository orderRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CartViewModel.class)) {
            return (T) new CartViewModel(repository, discountRepository, orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
