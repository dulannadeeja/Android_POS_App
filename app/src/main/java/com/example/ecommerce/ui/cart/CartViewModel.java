package com.example.ecommerce.ui.cart;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;
import com.example.ecommerce.repository.IApplyDiscountCallback;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IOrderRepository;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartViewModel extends ViewModel {
    private final ICartRepository repository;
    private final IDiscountRepository discountRepository;
    private final IOrderRepository orderRepository;
    private static final String TAG = "cartViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<Cart> cart = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CartViewModel(ICartRepository repository, IDiscountRepository discountRepository, IOrderRepository orderRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.orderRepository = orderRepository;
        onFetchCart();
        onApplyCurrentDiscount();
    }

    public void onFetchCart() {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.getCartHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((fetchedCart) ->{
                            cart.postValue(fetchedCart);
                            errorMessage.postValue("");
                        }, throwable -> {
                            Log.e("cartViewModel", "Error fetching cart", throwable);
                            errorMessage.postValue("Error fetching cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onApplyCurrentDiscount() {
        isLoading.setValue(true);

        if(cart == null || cart.getValue() == null) {
            isLoading.setValue(false);
            return;
        }

        compositeDisposable.add(
                discountRepository.applyCurrentDiscountHandler(Objects.requireNonNull(cart.getValue()).getCartSubTotalPrice())
                        .flatMapCompletable(currentDiscount -> {
                            int discountId = Integer.parseInt(Objects.requireNonNull(currentDiscount.get("discountId")).toString());
                            double discountAmount = Double.parseDouble(Objects.requireNonNull(currentDiscount.get("discountAmount")).toString());
                            Log.d(TAG, "Applying discount: " + discountId + " with amount: " + discountAmount);
                            // Apply the discount and then fetch the updated cart
                            return repository.applyDiscountToCart(discountId, discountAmount);
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart();
                                    errorMessage.postValue("");
                                },
                                throwable -> {
                                    Log.e("cartViewModel", "Error applying discount", throwable);
                                    errorMessage.postValue("Error applying discount");
                                }
                        )
        );

        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onAddToCart(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true);

        compositeDisposable.add(
                repository.addProductToCart(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();
                            errorMessage.postValue("");
                            callback.onSuccessfulCartOperation();
                        }, throwable -> {
                            errorMessage.postValue("Error adding product to cart");
                            callback.onFailedCartOperation("Looks like the product is out of stock");
                        }));

        // Ensure the loading state is reset
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onDecrementProductQuantity(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.decrementProductQuantity(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();
                            errorMessage.postValue("");
                            callback.onSuccessfulCartOperation();
                        }, throwable -> {
                            Log.e("cartViewModel", "Error decreasing product quantity", throwable);
                            errorMessage.postValue("Something went wrong, please try again");
                            callback.onFailedCartOperation("Something went wrong, please try again");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onRemoveFromCart(int productId) {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.removeProductFromCart(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();
                            errorMessage.postValue("");
                        }, throwable -> {
                            Log.e("cartViewModel", "Error removing product from cart", throwable);
                            errorMessage.postValue("Error removing product from cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onClearCart() {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.clearCart()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();
                            errorMessage.postValue("");
                        }, throwable -> {
                            Log.e("cartViewModel", "Error clearing cart", throwable);
                            errorMessage.postValue("Error clearing cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onSavePendingOrder(OnSavedPendingOrderCallback callback) {
        try {
            int customerId = 0;
            if (App.appModule.provideCustomerSharedPreferences().contains("activeCustomerId")) {
                customerId = Integer.parseInt(App.appModule.provideCustomerSharedPreferences().getString("activeCustomerId", ""));
                long orderId = orderRepository.handleNewOrder(cart.getValue().getCartItems(), cart.getValue().getCartTotalPrice(), cart.getValue().getCartTotalTaxAndCharges(), cart.getValue().getCartSubTotalPrice(), cart.getValue().getDiscountId(), cart.getValue().getDiscountValue(), customerId);
                callback.onSuccessfulOrderSaved();
            } else {
                callback.onFailedOrderSaved("Please add a customer to save order");
            }
        } catch (Exception e) {
            Log.e(TAG, "Order save failed! ", e);
            callback.onFailedOrderSaved("Error occurred while saving order");
        }
    }

    public void onLoadOpenOrderToCart(Order order) {
        try {
            isLoading.setValue(true);
            repository.clearCart();
            ArrayList<OrderItem> orderItems = orderRepository.getOrderItems(order.get_orderId());
            orderItems.forEach(orderItem -> {
                int itemQty = orderItem.getQuantity();
                for (int i = 0; i < itemQty; i++) {
                    try {
                        repository.addProductToCart(orderItem.getProductId());
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            onFetchCart();
        } catch (Exception e) {
            Log.e(TAG, "Error loading open order to cart", e);
            errorMessage.setValue("Error loading open order to cart");
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    public void dispose() {
        Log.d(TAG, "disposing cart view model threads");
        compositeDisposable.clear();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
