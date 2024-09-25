package com.example.ecommerce.features.open_orders;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.features.order.OnLoadCartCallback;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.OpenOrderItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.utils.DateHelper;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class OpenOrdersViewModel extends ViewModel {
    private static final String TAG = "OpenOrdersViewModel";
    private final IOrderRepository orderRepository;
    private final IProductRepository productRepository;
    private final MutableLiveData<ArrayList<OpenOrderItem>> ordersListLiveData = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public OpenOrdersViewModel(IOrderRepository orderRepository, IProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public void setPendingOrders(ArrayList<Order> pendingOrders) {
        try {
            Map<String, List<Order>> ordersByDate = pendingOrders.stream()
                    .collect(Collectors.groupingBy(order -> DateHelper.formatDate(order.getOrderDate()), LinkedHashMap::new, Collectors.toList()));

            ArrayList<OpenOrderItem> ordersList = new ArrayList<>();
            ordersByDate.forEach((date, orders) -> {
                ordersList.add(new OpenOrderItem(date));
                orders.forEach(order -> ordersList.add(new OpenOrderItem(order)));
            });

            ordersListLiveData.setValue(ordersList);
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending orders", e);
        }
    }

    public void onLoadPendingOrder(Order order,OnLoadCartCallback onLoadCartCallback) {
        ArrayList<CartItem> cartItems = new ArrayList<>();

        compositeDisposable.add(
                orderRepository.getOrderItems(order.get_orderId()).flatMapCompletable(orderItems ->
                        Observable.fromIterable(orderItems)
                                .flatMapSingle(orderItem ->
                                        productRepository.getProductById(orderItem.getProductId())
                                                .map(product -> new CartItem.Builder(orderItem.getProductId())
                                                        .withProductName(product.getProductName())
                                                        .withQuantity(orderItem.getQuantity())
                                                        .withPrice(product.getProductPrice())
                                                        .withDiscount(product.getProductDiscount())
                                                        .build())
                                )
                                .toList()
                                .flatMapCompletable(cartItemList -> Completable.fromAction(() -> cartItems.addAll(cartItemList)))
                )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            Cart cartToLoad = new Cart.CartBuilder()
                                    .withCartSubTotalPrice(order.getSubTotal())
                                    .withCartTotalTaxAndCharges(order.getTaxAndCharges())
                                    .withOrderId(order.get_orderId())
                                    .withDiscount(order.getDiscountId(), order.getDiscountAmount())
                                    .withCartItems(cartItems)
                                    .build();
                            onLoadCartCallback.onLoadCart(cartToLoad);
                        }, throwable -> {
                            Log.e(TAG, "Error loading pending order", throwable);
                        })
        );
    }

    public MutableLiveData<ArrayList<OpenOrderItem>> getOrdersListLiveData() {
        return ordersListLiveData;
    }

}
