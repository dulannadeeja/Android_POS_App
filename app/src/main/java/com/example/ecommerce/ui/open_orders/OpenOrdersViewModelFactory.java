package com.example.ecommerce.ui.open_orders;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.IOrderRepository;

public class OpenOrdersViewModelFactory implements ViewModelProvider.Factory {
    private final IOrderRepository orderRepository;

    public OpenOrdersViewModelFactory(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OpenOrdersViewModel.class)) {
            return (T) new OpenOrdersViewModel(orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
