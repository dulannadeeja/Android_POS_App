package com.example.ecommerce.features.open_orders;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IProductRepository;

public class OpenOrdersViewModelFactory implements ViewModelProvider.Factory {
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;

    public OpenOrdersViewModelFactory(IOrderRepository orderRepository, IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(OpenOrdersViewModel.class)) {
            return (T) new OpenOrdersViewModel(orderRepository, productRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}
