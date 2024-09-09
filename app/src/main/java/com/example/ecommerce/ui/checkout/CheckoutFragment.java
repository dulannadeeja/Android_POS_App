package com.example.ecommerce.ui.checkout;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.databinding.FragmentCheckoutBinding;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.cart.CartViewModel;
import com.example.ecommerce.ui.cart.CartViewModelFactory;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.ui.summary.SummaryFragment;
import com.example.ecommerce.utils.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class CheckoutFragment extends Fragment {

    private CheckoutViewModel checkoutViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity) getActivity()).loadFragment(new CartFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCheckoutBinding binding = FragmentCheckoutBinding.bind(view);

        MaterialToolbar toolbar = view.findViewById(R.id.checkout_toolbar);
        toolbar.inflateMenu(R.menu.menu_checkout_appbar);
        toolbar.setTitle("Payment");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d("ProductsFragment", "onOptionsItemSelected");
            if (item.getItemId() == R.id.new_user) {
                Toast.makeText(getContext(), "New user clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate somewhere.
                ((MainActivity) getActivity()).loadFragment(new CartFragment());
            }
        });

        // Initialize the checkout view model
        checkoutViewModel = new ViewModelProvider(this, App.appModule.provideCheckoutViewModelFactory()).get(CheckoutViewModel.class);

        // Observe the cart data
        checkoutViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            binding.tvTotalAmount.setText(String.valueOf(cart.getCartSubTotalPrice()));
            binding.etAmountDue.setText(String.valueOf(cart.getCartSubTotalPrice()));
        });

        // Observe the payment method
        checkoutViewModel.getPaymentMethod().observe(getViewLifecycleOwner(), paymentMethod -> {
            if(paymentMethod.equals("CARD")) {
                binding.selectCardButton.setBackgroundColor(getResources().getColor(R.color.accentColor));
                binding.selectCashButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                binding.selectCreditButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
            }else if(paymentMethod.equals("CASH")) {
                binding.selectCardButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                binding.selectCashButton.setBackgroundColor(getResources().getColor(R.color.accentColor));
                binding.selectCreditButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
            }else if(paymentMethod.equals("CREDIT")) {
                binding.selectCardButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                binding.selectCashButton.setBackgroundColor(getResources().getColor(R.color.backgroundColor));
                binding.selectCreditButton.setBackgroundColor(getResources().getColor(R.color.accentColor));
            }
        });

        binding.selectCardButton.setOnClickListener(v -> {
            checkoutViewModel.onPaymentMethodChanged("CARD");
        });

        binding.selectCashButton.setOnClickListener(v -> {
            checkoutViewModel.onPaymentMethodChanged("CASH");
        });

        binding.selectCreditButton.setOnClickListener(v -> {
            checkoutViewModel.onPaymentMethodChanged("CREDIT");
        });

        binding.chargeButton.setOnClickListener(v -> {
            checkoutViewModel.onOrderConfirmed(new OnOrderPlacedCallback() {
                @Override
                public void onSuccessfulOrderPlaced() {
                    ((MainActivity) getActivity()).loadFragment(new SummaryFragment());
                    Toast.makeText(getContext(), "Order placed successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailedOrderPlaced() {
                    Toast.makeText(getContext(), "Order placement failed", Toast.LENGTH_SHORT).show();
                }
            });
        });

        // listen for the due amount changes
        binding.etAmountDue.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                try {
                    double amountDue = Double.parseDouble(s.toString());
                    checkoutViewModel.setPayingAmount(amountDue);
                } catch (NumberFormatException e) {
                    Log.e("CheckoutFragment", "Invalid amount due", e);
                }
            }
        });
    }
}