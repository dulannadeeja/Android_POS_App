package com.example.ecommerce.features.cart;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IProductRepository;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CartViewModel extends ViewModel {
    private final ICartRepository repository;
    private final IDiscountRepository discountRepository;
    private final IProductRepository productRepository;
    private static final String TAG = "cartViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<Cart> cart = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    public CartViewModel(ICartRepository repository, IDiscountRepository discountRepository, IProductRepository productRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
        onFetchCart();
    }

    public void onFetchCart() {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.getCartHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe((fetchedCart) ->{
                            cart.setValue(fetchedCart);
                            onApplyCurrentDiscount();
                            errorMessage.setValue("");
                        }, throwable -> {
                            Log.e(TAG, "Error fetching cart", throwable);
                            errorMessage.setValue("Error fetching cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onApplyCurrentDiscount() {
        isLoading.setValue(true);

        double cartSubTotalPrice = cart.getValue() == null ? 0.0 : Objects.requireNonNull(cart.getValue()).getCartSubTotalPrice();
        AtomicInteger discountId = new AtomicInteger(-1);
        AtomicReference<Double> discountAmount = new AtomicReference<>(0.0);

        compositeDisposable.add(
                discountRepository.applyCurrentDiscountHandler(cartSubTotalPrice)
                        .flatMapCompletable(currentDiscount -> {
                            discountId.set(Integer.parseInt(Objects.requireNonNull(currentDiscount.get("discountId")).toString()));
                            discountAmount.set(Double.parseDouble(Objects.requireNonNull(currentDiscount.get("discountAmount")).toString()));

                            Log.d(TAG, "Applying discount to the cart: " + discountId + " with amount: " + discountAmount);

                            // Apply the discount and then fetch the updated cart
                            return repository.applyDiscountToCart(discountId.get(), discountAmount.get());
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    errorMessage.setValue("");
                                    Cart updatedCart = new Cart.CartBuilder()
                                            .withCartSubTotalPrice(cart.getValue().getCartSubTotalPrice())
                                            .withCartTotalTaxAndCharges(cart.getValue().getCartTotalTaxAndCharges())
                                            .withOrderId(cart.getValue().getOrderId())
                                            .withDiscount(discountId.get(), discountAmount.get())
                                            .withCartItems(Objects.requireNonNull(cart.getValue()).getCartItems())
                                            .build();
                                    cart.setValue(updatedCart);
                                },
                                throwable -> {
                                    Log.e(TAG, "Error applying discount", throwable);
                                    errorMessage.setValue("Error applying discount");
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
                            errorMessage.setValue("");
                            callback.onSuccessfulCartOperation();
                        }, throwable -> {
                            errorMessage.setValue("Error adding product to cart");
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
                            errorMessage.setValue("");
                            callback.onSuccessfulCartOperation();
                        }, throwable -> {
                            Log.e(TAG, "Error decreasing product quantity", throwable);
                            errorMessage.setValue("Something went wrong, please try again");
                            callback.onFailedCartOperation("Something went wrong, please try again");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onRemoveFromCart(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.removeProductFromCart(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            callback.onSuccessfulCartOperation();
                            onFetchCart();
                            errorMessage.setValue("");
                        }, throwable -> {
                            Log.e(TAG, "Error removing product from cart", throwable);
                            errorMessage.setValue("Error removing product from cart");
                            callback.onFailedCartOperation("Error removing product from cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public Completable addItemQuantityFromCartToStock(int productId) {
        int qtyInCart = 0;
        qtyInCart = cart.getValue().getCartItems().stream()
                .filter(cartItem -> cartItem.getProductId() == productId)
                .map(cartItem -> cartItem.getQuantity())
                .findFirst().orElse(0);
        return productRepository.increaseProductQuantity(productId, qtyInCart);
    }

    public void onClearCart() {
        isLoading.setValue(true);
        compositeDisposable.add(
                repository.clearCart()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();
                            errorMessage.setValue("");
                        }, throwable -> {
                            Log.e(TAG, "Error clearing cart", throwable);
                            errorMessage.setValue("Error clearing cart");
                        }));
        errorMessage.setValue("");
        isLoading.setValue(false);
    }

    public void onLoadOpenOrderToCart(Cart openOrderCart) {
        compositeDisposable.add(
                repository.clearCart()  // First clear the cart
                        .andThen(repository.saveCartHandler(openOrderCart, true))  // Then save the new cart
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(() -> {
                            onFetchCart();  // Fetch the updated cart
                            errorMessage.setValue("");
                        }, throwable -> {
                            Log.e(TAG, "Error loading open order to cart", throwable);
                            errorMessage.setValue("Error loading open order to cart");
                        })
        );
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

    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear();
    }

}
