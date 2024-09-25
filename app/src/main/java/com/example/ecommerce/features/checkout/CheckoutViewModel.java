package com.example.ecommerce.features.checkout;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.App;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.repository.IPaymentRepository;

public class CheckoutViewModel extends ViewModel {
    private static final String TAG = "CheckoutViewModel";
    private final ICartRepository cartRepository;
    private final IPaymentRepository paymentRepository;
    private final IOrderRepository orderRepository;
    private SharedPreferences sharedPreferences = App.appModule.provideAppContext()
            .getSharedPreferences("CustomerPrefs", App.appModule.provideAppContext().MODE_PRIVATE);


    private MutableLiveData<String> paymentMethod = new MutableLiveData<>("CASH");
    private MutableLiveData<Cart> cart = new MutableLiveData<>();
    private MutableLiveData<Double> payingAmount = new MutableLiveData<>(0.0);

    public CheckoutViewModel(ICartRepository cartRepository, IPaymentRepository paymentRepository, IOrderRepository orderRepository) {
        this.cartRepository = cartRepository;
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
        init();
    }

    public void init(){
        try{
//            Cart cart = cartRepository.getCart();
//            this.cart.setValue(cart);
//            payingAmount.setValue(cart.getCartSubTotalPrice());
        }catch (Exception e){
            Log.e(TAG, "Checkout init failed! ", e);
        }
    }

    public void onPaymentMethodChanged(String paymentMethod){
        this.paymentMethod.setValue(paymentMethod);
    }

    public void onOrderConfirmed(OnOrderPlacedCallback callback){
        // TODO: Implement order confirmation
    }

    public MutableLiveData<String> getPaymentMethod() {
        return paymentMethod;
    }

    public MutableLiveData<Cart> getCart() {
        return cart;
    }

    public MutableLiveData<Double> getPayingAmount() {
        return payingAmount;
    }

    public void setPayingAmount(Double payingAmount) {
        this.payingAmount.setValue(payingAmount);
    }

}
