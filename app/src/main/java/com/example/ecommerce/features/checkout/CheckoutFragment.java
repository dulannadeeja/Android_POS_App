package com.example.ecommerce.features.checkout;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentCheckoutBinding;
import com.example.ecommerce.features.cart.CartFragment;
import com.example.ecommerce.features.summary.SummaryFragment;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.utils.OnCompletableFinishedCallback;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Locale;

public class CheckoutFragment extends DialogFragment {

    private static final String TAG = "CheckoutFragment";
    private static final String ARG_ORDER_ID = "order_id";  // Key for the order ID
    private static final String ARG_CART = "cart";  // Key for the cart
    private static final String ARG_CUSTOMER = "customer";  // Key for the customer
    private CheckoutViewModel checkoutViewModel;
    private int orderId;
    private Cart cartToSave;
    private Customer customer;

    public static CheckoutFragment newInstance(Cart cartToSave, Customer customer, int orderId) {
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        args.putParcelable(ARG_CART, cartToSave);
        args.putParcelable(ARG_CUSTOMER, customer);

        CheckoutFragment fragment = new CheckoutFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the dialog to full-screen width and height
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID);
            customer = getArguments().getParcelable(ARG_CUSTOMER, Customer.class);
            cartToSave = getArguments().getParcelable(ARG_CART, Cart.class);
        }

        FragmentCheckoutBinding binding = FragmentCheckoutBinding.bind(view);

        MaterialToolbar toolbar = view.findViewById(R.id.checkout_toolbar);
        toolbar.inflateMenu(R.menu.menu_checkout_appbar);
        toolbar.setTitle("Payment");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.new_user) {
                Toast.makeText(getContext(), "New user clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        // Initialize the checkout view model
        checkoutViewModel = new ViewModelProvider(this, App.appModule.provideCheckoutViewModelFactory()).get(CheckoutViewModel.class);

        // Set the cart and customer data
        checkoutViewModel.init(orderId, cartToSave, customer, new OnCompletableFinishedCallback() {
            @Override
            public void onComplete(boolean isSuccess, String message) {
                if(!isSuccess){
                    dismiss();
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.chargeButton.setOnClickListener(v -> {
            checkoutViewModel.onCharge(new OnCompletableFinishedCallback() {
                @Override
                public void onComplete(boolean isSuccess, String message) {
                    if(isSuccess){
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        double totalPaidAmount = checkoutViewModel.getPayingAmount().getValue();
                        double changeAmount = checkoutViewModel.getChangeAmount().getValue();
                        SummaryFragment summaryFragment = SummaryFragment.newInstance(totalPaidAmount, changeAmount);
                        summaryFragment.show(getParentFragmentManager(), SummaryFragment.class.getSimpleName());
                        dismiss();
                    } else {
                        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        binding.etAmountDue.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        binding.etAmountDue.addTextChangedListener(new TextWatcher() {
            private String currentText = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Ensure we don't perform formatting if the text hasn't changed
                String newText = s.toString();
                Log.d(TAG, "New Text Comes In: " + newText);
                if (!newText.equals(currentText)) {
                    binding.etAmountDue.removeTextChangedListener(this);

                    // Handle number formatting, preserving decimal up to 2 digits
                    String formattedValue = formatAsATMInput(newText);

                    Log.d(TAG, "Formatted Value: " + formattedValue);

                    // Update the current text to avoid recursive calls
                    currentText = formattedValue;

                    // Set the formatted text and move the cursor to the correct position
                    binding.etAmountDue.setText(formattedValue);
                    binding.etAmountDue.setSelection(formattedValue.length());

                    // Set the amount in view model
                    String valueWithoutCommas = formattedValue.replace(",", "");
                    checkoutViewModel.setPayingAmount(Double.parseDouble(valueWithoutCommas));

                    binding.etAmountDue.addTextChangedListener(this);
                }
            }

            /**
             * Formats the input text to behave like ATM input:
             * - Max 2 decimal places.
             * - Right to left number insertion with auto decimal placement.
             */
            private String formatAsATMInput(String input) {
                // Remove any non-numeric characters (but keep the digits)
                String cleanInput = input.replaceAll("[^\\d]", "");

                Log.d(TAG, "Clean Input: " + cleanInput);

                // If the input is empty, return "0.00"
                if (cleanInput.isEmpty()) {
                    return "0.00";
                }

                // Parse the clean input as a long to avoid precision issues
                long parsedValue = Long.parseLong(cleanInput);

                // Format the value as a decimal number with 2 decimal places
                return String.format(Locale.US, "%,.2f", parsedValue / 100.0);
            }
        });

        // Observe the view model for changes
        checkoutViewModel.getOrder().observe(getViewLifecycleOwner(), order -> {
            if (order != null) {
                binding.tvTotalAmount.setText(String.format(Locale.US, "$%,.2f", order.getDueAmount()));
                binding.etAmountDue.setText(String.valueOf(order.getDueAmount() * 10));
            }
        });

        checkoutViewModel.getPayingAmount().observe(getViewLifecycleOwner(), payingAmount -> {
            double dueAmount = checkoutViewModel.getOrder().getValue() != null ? checkoutViewModel.getOrder().getValue().getDueAmount() : 0;
            if (payingAmount == null || payingAmount < dueAmount || payingAmount == 0.0) {
                binding.etAmountDue.setError("Amount must be greater than due amount");
                binding.chargeButton.setEnabled(false);
            }else {
                binding.chargeButton.setEnabled(true);
                binding.etAmountDue.setError(null);
            }
            checkoutViewModel.setChangeAmount();
        });

        checkoutViewModel.getChangeAmount().observe(getViewLifecycleOwner(), changeAmount -> {
            if(changeAmount > 0){
                binding.tvChangeAmount.setText(String.format(Locale.US, "$%,.2f", changeAmount));
                binding.changeContainer.setVisibility(View.VISIBLE);
                binding.divider.setVisibility(View.VISIBLE);
            } else {
                binding.changeContainer.setVisibility(View.GONE);
                binding.divider.setVisibility(View.GONE);
            }
        });
    }
}