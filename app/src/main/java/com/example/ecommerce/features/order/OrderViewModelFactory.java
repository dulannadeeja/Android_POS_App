package com.example.ecommerce.features.order;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.IOrderRepository;

public class OrderViewModelFactory implements ViewModelProvider.Factory {
    private final IOrderRepository orderRepository;

    public OrderViewModelFactory(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OrderViewModel.class)) {
            return (T) new OrderViewModel(orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
