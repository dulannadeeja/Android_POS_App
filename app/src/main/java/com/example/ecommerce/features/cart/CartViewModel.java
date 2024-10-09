package com.example.ecommerce.features.cart;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.repository.IProductRepository;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

/**
 * ViewModel for managing the cart-related UI logic.
 * This class is responsible for fetching, updating, and maintaining the cart state.
 */
public class CartViewModel extends ViewModel {
    private final ICartRepository repository; // Repository for cart operations
    private final IDiscountRepository discountRepository; // Repository for discount operations
    private final IProductRepository productRepository; // Repository for product operations
    private static final String TAG = "CartViewModel"; // Tag for logging
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false); // Loading state
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>(""); // Error message
    private static final MutableLiveData<Cart> cart = new MutableLiveData<>(); // Cart data
    private final CompositeDisposable compositeDisposable = new CompositeDisposable(); // Disposable for managing RxJava subscriptions

    /**
     * Constructor for CartViewModel.
     * Initializes the repositories and fetches the cart data.
     *
     * @param repository         Cart repository
     * @param discountRepository  Discount repository
     * @param productRepository   Product repository
     */
    public CartViewModel(ICartRepository repository, IDiscountRepository discountRepository, IProductRepository productRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        this.productRepository = productRepository;
        onFetchCart(); // Fetch the cart upon initialization
    }

    /**
     * Fetches the current cart from the repository.
     * Updates the UI state accordingly.
     */
    public void onFetchCart() {
        isLoading.setValue(true); // Set loading state to true
        compositeDisposable.add(
                repository.getCartHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fetchedCart -> {
                                    cart.setValue(fetchedCart); // Update cart data
                                    onApplyCurrentDiscount(); // Apply any current discount
                                    errorMessage.setValue(""); // Clear any error messages
                                },
                                throwable -> {
                                    Log.e(TAG, "Error fetching cart", throwable);
                                    errorMessage.setValue("Error fetching cart"); // Set error message
                                }
                        )
        );

        // Ensure the loading state is reset
        isLoading.setValue(false);
    }

    /**
     * Applies the current discount to the cart.
     */
    public void onApplyCurrentDiscount() {
        isLoading.setValue(true); // Set loading state to true

        double cartSubTotalPrice = cart.getValue() == null ? 0.0 : Objects.requireNonNull(cart.getValue()).getCartSubTotalPrice();
        AtomicInteger discountId = new AtomicInteger(-1);
        AtomicReference<Double> discountAmount = new AtomicReference<>(0.0);

        compositeDisposable.add(
                discountRepository.applyCurrentDiscountHandler(cartSubTotalPrice)
                        .flatMapCompletable(currentDiscount -> {
                            // Extract discount details
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
                                    errorMessage.setValue(""); // Clear any error messages
                                    // Update the cart with the applied discount
                                    Cart updatedCart = new Cart.CartBuilder()
                                            .withCartSubTotalPrice(cart.getValue().getCartSubTotalPrice())
                                            .withCartTotalTaxAndCharges(cart.getValue().getCartTotalTaxAndCharges())
                                            .withOrderId(cart.getValue().getOrderId())
                                            .withDiscount(discountId.get(), discountAmount.get())
                                            .withCartItems(Objects.requireNonNull(cart.getValue()).getCartItems())
                                            .build();
                                    cart.setValue(updatedCart); // Update cart state
                                },
                                throwable -> {
                                    Log.e(TAG, "Error applying discount", throwable);
                                    errorMessage.setValue("Error applying discount"); // Set error message
                                }
                        )
        );

        isLoading.setValue(false); // Reset loading state
    }

    /**
     * Adds a product to the cart.
     *
     * @param productId The ID of the product to be added.
     * @param callback  Callback to handle operation completion.
     */
    public void onAddToCart(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true); // Set loading state to true

        compositeDisposable.add(
                repository.addProductToCart(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart(); // Refresh cart
                                    errorMessage.setValue(""); // Clear any error messages
                                    callback.onSuccessfulCartOperation(); // Notify success
                                },
                                throwable -> {
                                    errorMessage.setValue("Error adding product to cart"); // Set error message
                                    callback.onFailedCartOperation("Looks like the product is out of stock"); // Notify failure
                                }
                        )
        );

        isLoading.setValue(false); // Reset loading state
    }

    /**
     * Decrements the quantity of a product in the cart.
     *
     * @param productId The ID of the product to be decremented.
     * @param callback  Callback to handle operation completion.
     */
    public void onDecrementProductQuantity(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true); // Set loading state to true
        compositeDisposable.add(
                repository.decrementProductQuantity(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart(); // Refresh cart
                                    errorMessage.setValue(""); // Clear any error messages
                                    callback.onSuccessfulCartOperation(); // Notify success
                                },
                                throwable -> {
                                    Log.e(TAG, "Error decreasing product quantity", throwable);
                                    errorMessage.setValue("Something went wrong, please try again"); // Set error message
                                    callback.onFailedCartOperation("Something went wrong, please try again"); // Notify failure
                                }
                        )
        );

        isLoading.setValue(false); // Reset loading state
    }

    /**
     * Removes a product from the cart.
     *
     * @param productId The ID of the product to be removed.
     * @param callback  Callback to handle operation completion.
     */
    public void onRemoveFromCart(int productId, OnCartOperationCompleted callback) {
        isLoading.setValue(true); // Set loading state to true
        compositeDisposable.add(
                repository.removeProductFromCart(productId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart(); // Refresh cart
                                    errorMessage.setValue(""); // Clear any error messages
                                    callback.onSuccessfulCartOperation(); // Notify success
                                },
                                throwable -> {
                                    Log.e(TAG, "Error removing product from cart", throwable);
                                    errorMessage.setValue("Error removing product from cart"); // Set error message
                                    callback.onFailedCartOperation("Error removing product from cart"); // Notify failure
                                }
                        )
        );

        isLoading.setValue(false); // Reset loading state
    }

    /**
     * Clears the entire cart.
     */
    public void onClearCart() {
        isLoading.setValue(true); // Set loading state to true
        compositeDisposable.add(
                repository.clearCart()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart(); // Refresh cart
                                    errorMessage.setValue(""); // Clear any error messages
                                },
                                throwable -> {
                                    Log.e(TAG, "Error clearing cart", throwable);
                                    errorMessage.setValue("Error clearing cart"); // Set error message
                                }
                        )
        );

        isLoading.setValue(false); // Reset loading state
    }

    /**
     * Loads an open order into the cart.
     *
     * @param openOrderCart The cart representing the open order to load.
     */
    public void onLoadOpenOrderToCart(Cart openOrderCart) {
        compositeDisposable.add(
                repository.clearCart() // First clear the cart
                        .andThen(repository.saveCartHandler(openOrderCart, true)) // Then save the new cart
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    onFetchCart(); // Refresh cart
                                    errorMessage.setValue(""); // Clear any error messages
                                },
                                throwable -> {
                                    Log.e(TAG, "Error loading open order to cart", throwable);
                                    errorMessage.setValue("Error loading open order to cart"); // Set error message
                                }
                        )
        );
    }

    /**
     * Gets the loading state of the ViewModel.
     *
     * @return MutableLiveData<Boolean> representing the loading state.
     */
    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    /**
     * Gets the current error message, if any.
     *
     * @return MutableLiveData<String> representing the error message.
     */
    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    /**
     * Gets the current cart data.
     *
     * @return MutableLiveData<Cart> representing the current cart.
     */
    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    /**
     * Clears all disposables when the ViewModel is cleared.
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        compositeDisposable.clear(); // Clear any ongoing subscriptions
    }
}
