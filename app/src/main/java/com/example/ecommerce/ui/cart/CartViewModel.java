package com.example.ecommerce.ui.cart;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;

public class CartViewModel extends ViewModel {
    private final ICartRepository repository;
    private final IDiscountRepository discountRepository;
    private static final String TAG = "cartViewModel";
    private static final MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private static final MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private static final MutableLiveData<Cart> cart = new MutableLiveData<>();

    public CartViewModel(ICartRepository repository, IDiscountRepository discountRepository) {
        this.repository = repository;
        this.discountRepository = discountRepository;
        setCart();
        setDiscount();
    }

    public void setCart() {
        try {
            isLoading.setValue(true);
            cart.setValue(repository.getCart());
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
            isLoading.setValue(true);
            Discount currentDiscount = discountRepository.getCurrentDiscount();
            if(currentDiscount != null && cart.getValue() != null) {
                double discountAmount = discountRepository.getDiscountAmount(cart.getValue().getCartTotalPrice());
                cart.getValue().setDiscountValue(discountAmount);
                cart.getValue().setDiscountId(currentDiscount.getDiscountId());
                cart.getValue().calculateCartTotalPrice();
            }
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error fetching discount", e);
            errorMessage.setValue("Error fetching discount");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onAddToCart(int productId) {
        try {
            isLoading.setValue(true);
            repository.addProductToCart(productId);
            cart.setValue(repository.getCart());
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error adding product to cart", e);
            errorMessage.setValue("Error adding product to cart");
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onRemoveFromCart(int productId) {
        try {
            isLoading.setValue(true);
            repository.removeProductFromCart(productId);
            cart.setValue(repository.getCart());
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
            repository.clearCart();
            cart.setValue(repository.getCart());
            errorMessage.setValue("");
        } catch (Exception e) {
            Log.e("cartViewModel", "Error clearing cart", e);
            errorMessage.setValue("Error clearing cart");
        } finally {
            isLoading.setValue(false);
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

}
