package com.example.ecommerce.features.checkout;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IPaymentRepository;

public class CheckoutViewModelFactory implements ViewModelProvider.Factory {
    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;

    public CheckoutViewModelFactory(IPaymentRepository paymentRepository, IOrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CheckoutViewModel.class)) {
            return (T) new CheckoutViewModel(paymentRepository, orderRepository);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
