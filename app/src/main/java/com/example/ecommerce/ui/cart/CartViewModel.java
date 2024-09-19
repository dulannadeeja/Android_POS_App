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

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
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
    private final Boolean isOpenOrder = false;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CartViewModel(ICartRepository repository, IDiscountRepository discountRepository, IOrderRepository orderRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.orderRepository = orderRepository;
        setCart();
        setDiscount();
    }

    public void setCart() {
        try {
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.getCartHandler()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(cart::postValue, throwable -> {
                                Log.e("cartViewModel", "Error fetching cart", throwable);
                                errorMessage.setValue("Error fetching cart");
                            })
            );
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error fetching cart", e);
            errorMessage.setValue("Error fetching cart");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void setDiscount() {
        try {
            Log.d(TAG, "setDiscount:");
            isLoading.setValue(true);
            if (cart.getValue() == null || cart.getValue().getCartTotalPrice() == 0) {
                return;
            }
            discountRepository.getDiscountAmount(cart.getValue().getCartTotalPrice(), new IApplyDiscountCallback() {
                @Override
                public void onDiscountApplied(double discountAmount, int discountId) {
                    cart.getValue().setDiscountValue(discountAmount);
                    cart.getValue().setDiscountId(discountId);
                    cart.getValue().calculateCartTotalPrice();
                    cart.setValue(cart.getValue().clone());
                }

                @Override
                public void onDiscountError(String error) {
                    errorMessage.setValue(error);
                    cart.getValue().setDiscountValue(0);
                    cart.getValue().setDiscountId(-1);
                    cart.getValue().calculateCartTotalPrice();
                    cart.setValue(cart.getValue().clone());
                }
            });

            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error fetching discount", e);
            errorMessage.setValue("Error fetching discount");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onAddToCart(int productId, OnCartOperationCompleted callback) {
        try {
            Log.d(TAG, "onAddToCart: ");
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.addProductToCart(productId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                setCart();
                                setDiscount();
                                errorMessage.setValue("");
                                callback.onSuccessfulCartOperation();
                            }, throwable -> {
                                Log.e("cartViewModel", "Error adding product to cart", throwable);
                                errorMessage.setValue("Error adding product to cart");
                                callback.onFailedCartOperation("Looks like the product is out of stock");
                            })
            );
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error adding product to cart", e);
            errorMessage.setValue("Error adding product to cart");
            callback.onFailedCartOperation("Looks like the product is out of stock");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onDecrementProductQuantity(int productId, OnCartOperationCompleted callback) {
        try {
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.decrementProductQuantity(productId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                setCart();
                                setDiscount();
                                errorMessage.setValue("");
                                callback.onSuccessfulCartOperation();
                            }, throwable -> {
                                Log.e("cartViewModel", "Error decreasing product quantity", throwable);
                                errorMessage.setValue("Something went wrong, please try again");
                                callback.onFailedCartOperation("Something went wrong, please try again");
                            })
                    );
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error decreasing product quantity", e);
            errorMessage.setValue("Something went wrong, please try again");
            callback.onFailedCartOperation("Something went wrong, please try again");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onRemoveFromCart(int productId) {
        try {
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.removeProductFromCart(productId)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                setCart();
                                setDiscount();
                                errorMessage.setValue("");
                            }, throwable -> {
                                Log.e("cartViewModel", "Error removing product from cart", throwable);
                                errorMessage.setValue("Error removing product from cart");
                            })
            );
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error removing product from cart", e);
            errorMessage.setValue("Error removing product from cart");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onClearCart() {
        try {
            isLoading.setValue(true);
            compositeDisposable.add(
                    repository.clearCart()
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(() -> {
                                setCart();
                                errorMessage.setValue("");
                            }, throwable -> {
                                Log.e("cartViewModel", "Error clearing cart", throwable);
                                errorMessage.setValue("Error clearing cart");
                            })
            );
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error clearing cart", e);
            errorMessage.setValue("Error clearing cart");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onSavePendingOrder(OnSavedPendingOrderCallback callback) {
        try {
            int customerId = 0;
            if (App.appModule.provideCustomerSharedPreferences().contains("activeCustomerId")) {
                customerId = Integer.parseInt(App.appModule.provideCustomerSharedPreferences().getString("activeCustomerId", ""));
                long orderId = orderRepository.handleNewOrder(cart.getValue().getCartItems(), cart.getValue().getCartTotalPrice(), cart.getValue().getCartTotalTax(), cart.getValue().getCartSubTotalPrice(), cart.getValue().getDiscountId(), cart.getValue().getDiscountValue(), customerId);
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
            setCart();
            setDiscount();
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

    public void dispose () {
        Log.d(TAG,"disposing cart view model threads");
        compositeDisposable.clear();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
