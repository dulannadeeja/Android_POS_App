package com.example.ecommerce.utils;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.features.checkout.split.SplitPaymentViewModel;
import com.example.ecommerce.model.Payment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;

public class SplitPaymentsAdapter extends RecyclerView.Adapter<SplitPaymentsAdapter.ViewHolder> {
    private final ArrayList<Payment> splitPayments;
    public static SplitPaymentViewModel viewModel = null;

    public SplitPaymentsAdapter(ArrayList<Payment> splitPayments, SplitPaymentViewModel viewModel) {
        this.splitPayments = splitPayments;
        SplitPaymentsAdapter.viewModel = viewModel;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_payment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Payment payment = splitPayments.get(position);
        holder.bind(payment,position);
    }

    @Override
    public int getItemCount() {
        return splitPayments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final MaterialButton btnRemovePayment;
        private final MaterialButton chargePaymentButton;
        private final TextInputEditText etPaymentAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnRemovePayment = itemView.findViewById(R.id.remove_split_payment_button);
            chargePaymentButton = itemView.findViewById(R.id.charge_split_payment_button);
            etPaymentAmount = itemView.findViewById(R.id.et_split_payment_amount);
        }

        public void bind(Payment payment,int position) {
            String formattedPaymentAmount = String.format("%.2f", payment.getPaymentAmount());
            TextWatcher paymentAmountWatcher = new TextWatcher() {
                TextWatcher paymentAmountWatcher = this;

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    // No action needed here
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // No action needed here
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // Apply ATM-style number formatting
                    DigitalNumberInput filter = new DigitalNumberInput();
                    filter.onInputComesIn(s.toString(), (formattedValue, cursorPosition) -> {
                        // Remove TextWatcher before setting the text to avoid recursion
                        etPaymentAmount.removeTextChangedListener(paymentAmountWatcher);
                        // Set the formatted text and restore the cursor position
                        etPaymentAmount.setText(formattedValue);
                        etPaymentAmount.setSelection(cursorPosition);

                        // Re-attach TextWatcher after updating the text
                        etPaymentAmount.addTextChangedListener(paymentAmountWatcher);
                    });
                }
            };

            etPaymentAmount.removeTextChangedListener(paymentAmountWatcher);
            etPaymentAmount.setText(formattedPaymentAmount);
            etPaymentAmount.addTextChangedListener(paymentAmountWatcher);

            btnRemovePayment.setOnClickListener(v -> {
                // Remove payment from the list
            });
            chargePaymentButton.setOnClickListener(v -> {
                // Charge payment
            });
        }
    }

    public Completable onDataSetChanged(ArrayList<Payment> updatedSplitPayments) {
        return Completable.fromAction(() -> {
            synchronized (splitPayments) {
                Log.d("SplitPayment", "Before clearing - splitPayments size: " + splitPayments.size());

                // Make a deep copy of updatedSplitPayments to avoid shared references
                ArrayList<Payment> newPayments = new ArrayList<>(updatedSplitPayments);

                splitPayments.clear(); // Clear the list safely
                splitPayments.addAll(newPayments); // Add the new list

                Log.d("SplitPayment", "After adding - splitPayments size: " + splitPayments.size());

                // Post the RecyclerView update to the main thread using Handler
                new Handler(Looper.getMainLooper()).post(this::notifyDataSetChanged);
            }
        });
    }
}
