package com.example.ecommerce.features.checkout;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.features.order.OrderStatus;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IPaymentRepository;
import com.example.ecommerce.utils.DateHelper;
import com.example.ecommerce.utils.OnCompletableFinishedCallback;

import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CheckoutViewModel extends ViewModel {
    private static final String TAG = "CheckoutViewModel";
    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;


    private MutableLiveData<String> paymentMethod = new MutableLiveData<>("CASH");
    private MutableLiveData<Double> payingAmount = new MutableLiveData<>(0.0);
    private MutableLiveData<Double> changeAmount = new MutableLiveData<>(0.0);
    private MutableLiveData<Boolean> isOrderPlaced = new MutableLiveData<>(false);
    private MutableLiveData<Boolean> isOrderCompleted = new MutableLiveData<>(false);
    private MutableLiveData<Order> order = new MutableLiveData<>();

    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CheckoutViewModel(IPaymentRepository paymentRepository, IOrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    public void init(int orderId, Cart cartToSave, Customer customer, OnCompletableFinishedCallback callback) {
        if (orderId > 0) {
            compositeDisposable.add(orderRepository.getOrderById(orderId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(fetchedOrder -> {
                        order.setValue(fetchedOrder);
                        isOrderPlaced.setValue(true);
                    }, throwable -> {
                        Log.e(TAG, "Error fetching order", throwable);
                        callback.onComplete(false, throwable.getMessage());
                    }));
        } else if (cartToSave != null && customer != null) {
            order.setValue(buildOrder(cartToSave, customer, 0));
            isOrderPlaced.setValue(false);
        }
    }

    public void onCharge(OnCompletableFinishedCallback callback) {

        double payingAmount = this.payingAmount.getValue() > order.getValue().getDueAmount() ? order.getValue().getDueAmount() : this.payingAmount.getValue();

        if (Boolean.TRUE.equals(isOrderPlaced.getValue())) {
            compositeDisposable.add(
                    paymentRepository.paymentHandler(paymentMethod.getValue(), payingAmount, order.getValue().getOrderId())
                            .andThen(orderRepository.updateOrderPayment(payingAmount, order.getValue().getOrderId()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(updatedOrder -> {
                                        order.setValue(updatedOrder);
                                        if (updatedOrder.getDueAmount() == 0) {
                                            isOrderCompleted.setValue(true);
                                        }
                                        callback.onComplete(true, "Payment successful");
                                    }, throwable -> {
                                        Log.e(TAG, "Error making payment", throwable);
                                        callback.onComplete(false, throwable.getMessage());
                                    }
                            )
            );
        } else {
            compositeDisposable.add(
                    orderRepository.createPendingOrderHandler(order.getValue())
                            .flatMap(orderId -> {
                                order.getValue().setOrderId(orderId);
                                isOrderPlaced.postValue(true);
                                return paymentRepository.paymentHandler(paymentMethod.getValue(), payingAmount, orderId)
                                        .andThen(orderRepository.updateOrderPayment(payingAmount, orderId));
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(updatedOrder -> {
                                order.setValue(updatedOrder);
                                isOrderPlaced.setValue(true);
                                if (updatedOrder.getDueAmount() == 0) {
                                    isOrderCompleted.setValue(true);
                                }
                                callback.onComplete(true, "Order placed successfully");
                            }, throwable -> {
                                Log.e(TAG, "Error creating order", throwable);
                                callback.onComplete(false, throwable.getMessage());
                            }));
        }
    }

    public void onPaymentMethodChanged(String paymentMethod) {
        this.paymentMethod.setValue(paymentMethod);
    }

    public MutableLiveData<String> getPaymentMethod() {
        return paymentMethod;
    }

    public MutableLiveData<Double> getPayingAmount() {
        return payingAmount;
    }

    public void setPayingAmount(Double payingAmount) {
        this.payingAmount.setValue(payingAmount);
    }

    public MutableLiveData<Boolean> getIsOrderPlaced() {
        return isOrderPlaced;
    }

    public MutableLiveData<Boolean> getIsOrderCompleted() {
        return isOrderCompleted;
    }

    public MutableLiveData<Order> getOrder() {
        return order;
    }

    public MutableLiveData<Double> getChangeAmount() {
        return changeAmount;
    }

    public void setChangeAmount() {
        double changeAmount = this.payingAmount.getValue() - this.order.getValue().getDueAmount() > 0 ? this.payingAmount.getValue() - this.order.getValue().getDueAmount() : 0;
        this.changeAmount.setValue(changeAmount);
    }

    // Common method to build an Order object from Cart and Customer
    private Order buildOrder(Cart cartToSave, Customer customer, int orderId) {
        return new Order.OrderBuilder(
                DateHelper.getTimeStamp(),
                cartToSave.getCartTotalPrice(),
                OrderStatus.PENDING.getStatus(),
                cartToSave.getCartTotalTaxAndCharges(),
                cartToSave.getCartSubTotalPrice())
                .withOrderId(Math.max(orderId, 0))
                .withPayment(0, cartToSave.getCartTotalPrice())
                .withCustomerId(customer.getCustomerId())
                .withDiscount(cartToSave.getDiscountId(), cartToSave.getDiscountValue())
                .withOrderItems(new ArrayList<>(cartToSave.getCartItems().stream().map(
                        cartItem -> new OrderItem.OrderItemBuilder(orderId, cartItem.getProductId(),cartItem.getQuantity() )
                                .build()
                ).toList()))
                .build();
    }

}
