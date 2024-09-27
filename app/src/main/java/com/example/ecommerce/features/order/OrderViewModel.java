package com.example.ecommerce.features.order;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.utils.DateHelper;
import com.example.ecommerce.utils.OnCompletableFinishedCallback;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OrderViewModel extends ViewModel {
    private final IOrderRepository orderRepository;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private final MutableLiveData<Order> order = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isSavedOrder = new MutableLiveData<>(false);

    public OrderViewModel(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    // Common method to build an Order object from Cart and Customer
    private Order buildOrder(Cart cartToSave, Customer customer, int orderId) {
        String date = DateHelper.getTimeStamp();

        return new Order.OrderBuilder(
                date,
                cartToSave.getCartTotalPrice(),
                OrderStatus.PENDING.getStatus(),
                cartToSave.getCartTotalTaxAndCharges(),
                cartToSave.getCartSubTotalPrice())
                .withOrderId(Math.max(orderId, 0))
                .withPayment(0, cartToSave.getCartTotalPrice())
                .withCustomerId(customer.getCustomerId())
                .withDiscount(cartToSave.getDiscountId(), cartToSave.getDiscountValue())
                .withOrderItems(new ArrayList<>(cartToSave.getCartItems().stream().map(
                        cartItem -> new OrderItem(0, cartItem.getProductId(), cartItem.getQuantity())
                ).toList()))
                .build();
    }

    // Method to handle saving a new order
    private void handleNewOrderSave(Order order, Single<Integer> singleAction, OnCompletableFinishedCallback callback, String successMessage, String errorMessage) {
        isLoading.setValue(true);

        try {
            validateBeforeSavePendingOrder(order);
            Log.d("OrderViewModel", "Order validation successful");

            compositeDisposable.add(
                    singleAction
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    orderId -> {
                                        isSavedOrder.setValue(true);
                                        this.errorMessage.setValue("");
                                        callback.onComplete(true, successMessage);
                                    },
                                    throwable -> {
                                        this.errorMessage.setValue(errorMessage);
                                        callback.onComplete(false, errorMessage);
                                        Log.e("OrderViewModel", errorMessage, throwable);
                                    }
                            )
            );
        } catch (ValidationException e) {
            this.errorMessage.setValue(e.getMessage());
            callback.onComplete(false, e.getMessage());
        } catch (Exception e) {
            this.errorMessage.setValue("Error processing order");
            callback.onComplete(false, "Error processing order");
        } finally {
            isLoading.setValue(false);
        }
    }

    // Method to handle updating an order
    private void handleOrderUpdate(Order order, Completable completableAction, OnCompletableFinishedCallback callback, String successMessage, String errorMessage) {
        isLoading.setValue(true);

        try {
            validateBeforeSavePendingOrder(order);
            Log.d("OrderViewModel", "Order validation successful");

            compositeDisposable.add(
                    completableAction
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        isSavedOrder.setValue(true);
                                        this.errorMessage.setValue("");
                                        callback.onComplete(true, successMessage);
                                    },
                                    throwable -> {
                                        this.errorMessage.setValue(errorMessage);
                                        callback.onComplete(false, errorMessage);
                                        Log.e("OrderViewModel", errorMessage, throwable);
                                    }
                            )
            );
        } catch (ValidationException e) {
            this.errorMessage.setValue(e.getMessage());
            callback.onComplete(false, e.getMessage());
        } catch (Exception e) {
            this.errorMessage.setValue("Error processing order");
            callback.onComplete(false, "Error processing order");
        } finally {
            isLoading.setValue(false);
        }
    }

    // Method to save a pending order (uses Single)
    public void onSavePendingOrder(Cart cartToSave, Customer customer, OnCompletableFinishedCallback callback) {
        if (cartToSave == null || cartToSave.getCartItems().isEmpty()) {
            errorMessage.setValue("Cart is empty");
            callback.onComplete(false, "Cart is empty");
            return;
        }

        Order newOrder = buildOrder(cartToSave, customer, 0);
        handleNewOrderSave(newOrder, orderRepository.createPendingOrderHandler(newOrder), callback, "Order saved successfully", "Error saving order");
    }

    // Method to update a pending order (uses Completable)
    public void onUpdatePendingOrder(Cart cartToSave, Customer customer, OnCompletableFinishedCallback callback) {
        if (cartToSave == null || cartToSave.getCartItems().isEmpty()) {
            errorMessage.setValue("Cart is empty");
            callback.onComplete(false, "Cart is empty");
            return;
        }

        Order newOrder = buildOrder(cartToSave, customer, cartToSave.getOrderId());
        handleOrderUpdate(newOrder, orderRepository.updatePendingOrderHandler(newOrder), callback, "Order updated successfully", "Error updating order");
    }

//    public void onSavePendingOrder(Cart cartToSave, Customer customer,OnCompletableFinishedCallback callback) {
//        isLoading.setValue(true); // Start loading state
//
//        Log.d("OrderViewModel", "Order saving started");
//
//        // Create a new order object
//        if (cartToSave == null || cartToSave.getCartItems().isEmpty()) {
//            errorMessage.setValue("Cart is empty");
//            callback.onComplete(false, "Cart is empty");
//            isLoading.setValue(false);
//            return;
//        }
//
//        // Build a new order object
//        String date = DateHelper.getTimeStamp();
//
//        Order newOrder = new Order.OrderBuilder(
//                date,
//                cartToSave.getCartTotalPrice(),
//                OrderStatus.PENDING.getStatus(),
//                cartToSave.getCartTotalTaxAndCharges(),
//                cartToSave.getCartSubTotalPrice())
//                .withPayment(0, cartToSave.getCartTotalPrice())
//                .withCustomerId(customer.getCustomerId())
//                .withDiscount(cartToSave.getDiscountId(), cartToSave.getDiscountValue())
//                .withOrderItems(new ArrayList<>(cartToSave.getCartItems().stream().map(
//                        cartItem -> new OrderItem(0, cartItem.getProductId(), cartItem.getQuantity())
//                ).toList()))
//                .build();
//
//        try {
//            validateBeforeSavePendingOrder(newOrder);
//
//            Log.d("OrderViewModel", "Order validation successful");
//
//            // Proceed with creating the pending order
//            compositeDisposable.add(
//                    orderRepository.createPendingOrderHandler(newOrder)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    orderId -> {
//                                        isSavedOrder.setValue(true);
//                                        errorMessage.setValue("");
//                                        callback.onComplete(true, "Order saved successfully");
//                                    },
//                                    throwable -> {
//                                        errorMessage.setValue("Error saving order");
//                                        callback.onComplete(false, "Error saving order");
//                                    }
//                            )
//            );
//        } catch (ValidationException e) {
//            errorMessage.setValue(e.getMessage());
//            callback.onComplete(false, e.getMessage());
//        } catch (Exception e) {
//            errorMessage.setValue("Error saving order");
//            callback.onComplete(false, "Error saving order");
//        } finally {
//            isLoading.setValue(false);
//        }
//    }
//
//    public void onUpdatePendingOrder(Cart cartToSave, Customer customer,OnCompletableFinishedCallback callback){
//        isLoading.setValue(true); // Start loading state
//
//        Log.d("OrderViewModel", "Order updating started");
//
//        // Create a new order object
//        if (cartToSave == null || cartToSave.getCartItems().isEmpty()) {
//            errorMessage.setValue("Cart is empty");
//            callback.onComplete(false, "Cart is empty");
//            isLoading.setValue(false);
//            return;
//        }
//
//        // Build a new order object
//        String date = DateHelper.getTimeStamp();
//
//        Order newOrder = new Order.OrderBuilder(
//                date,
//                cartToSave.getCartTotalPrice(),
//                OrderStatus.PENDING.getStatus(),
//                cartToSave.getCartTotalTaxAndCharges(),
//                cartToSave.getCartSubTotalPrice())
//                .withPayment(0, cartToSave.getCartTotalPrice())
//                .withCustomerId(customer.getCustomerId())
//                .withDiscount(cartToSave.getDiscountId(), cartToSave.getDiscountValue())
//                .withOrderItems(new ArrayList<>(cartToSave.getCartItems().stream().map(
//                        cartItem -> new OrderItem(0, cartItem.getProductId(), cartItem.getQuantity())
//                ).toList()))
//                .build();
//
//        try {
//            validateBeforeSavePendingOrder(newOrder);
//
//            Log.d("OrderViewModel", "Order validation successful");
//
//            // Proceed with creating the pending order
//            compositeDisposable.add(
//                    orderRepository.updatePendingOrderHandler(newOrder)
//                            .subscribeOn(Schedulers.io())
//                            .observeOn(AndroidSchedulers.mainThread())
//                            .subscribe(
//                                    () -> {
//                                        isSavedOrder.setValue(true);
//                                        errorMessage.setValue("");
//                                        callback.onComplete(true, "Order updated successfully");
//                                    },
//                                    throwable -> {
//                                        errorMessage.setValue("Error updating order");
//                                        callback.onComplete(false, "Error updating order");
//                                    }
//                            )
//            );
//        } catch (ValidationException e) {
//            errorMessage.setValue(e.getMessage());
//            callback.onComplete(false, e.getMessage());
//        } catch (Exception e) {
//            errorMessage.setValue("Error updating order");
//            callback.onComplete(false, "Error updating order");
//        } finally {
//            isLoading.setValue(false);
//        }
//    }

    public void onLoadPendingOrders(OnOrdersFetchedCallback callback) {
        isLoading.setValue(true);
        compositeDisposable.add(
                orderRepository.getPendingOrders()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                orders -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("");
                                    callback.onOrdersFetched(orders);
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    errorMessage.setValue("Error loading pending orders");
                                    callback.onOrdersFetchFailed("Error loading pending orders");
                                }
                        )
        );
    }

    private void validateBeforeSavePendingOrder(Order order) throws ValidationException {
        validateOrderTotal(order.getOrderTotal());
        validateCustomer(order.getCustomerId());
        validateSubTotal(order.getSubTotal());
        validateTaxAndCharges(order.getTaxAndCharges());
        validateDiscount(order.getDiscountId(), order.getDiscountAmount());
        validateTotalAmount(order);
        validateDueAmount(order.getDueAmount());
        validateOrderStatus(order.getOrderStatus());
    }

    private void validateOrderTotal(double orderTotal) throws ValidationException {
        if (orderTotal <= 0) {
            throw new ValidationException("Order total must be greater than 0");
        }
    }

    private void validateCustomer(long customerId) throws ValidationException {
        if (customerId <= 0) {
            throw new ValidationException("Order must have a valid customer");
        }
    }

    private void validateSubTotal(double subTotal) throws ValidationException {
        if (subTotal <= 0) {
            throw new ValidationException("Order subtotal must be greater than 0");
        }
    }

    private void validateTaxAndCharges(double taxAndCharges) throws ValidationException {
        if (taxAndCharges < 0) {
            throw new ValidationException("Order tax and charges must be greater than or equal to 0");
        }
    }

    private void validateDiscount(long discountId, double discountAmount) throws ValidationException {
        if (discountId > 0 && discountAmount <= 0) {
            throw new ValidationException("Order discount amount must be greater than 0");
        }
    }

    private void validateTotalAmount(Order order) throws ValidationException {
        double calculatedTotal = order.getSubTotal() + order.getTaxAndCharges() - order.getDiscountAmount();
        if (calculatedTotal != order.getOrderTotal()) {
            throw new ValidationException("Order total must be equal to subtotal + tax and charges - discount amount");
        }
    }

    private void validateDueAmount(double dueAmount) throws ValidationException {
        if (dueAmount < 0) {
            throw new ValidationException("Order due amount must be greater than or equal to 0");
        }
    }

    private void validateOrderStatus(String orderStatus) throws ValidationException {
        if (orderStatus.isEmpty() || !Objects.equals(orderStatus, OrderStatus.PENDING.getStatus())) {
            throw new ValidationException("Order status must be 'Pending'");
        }
    }

    // Custom exception class for validation
    public static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Order> getOrder() {
        return order;
    }

    public MutableLiveData<Boolean> getIsSavedOrder() {
        return isSavedOrder;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }
}