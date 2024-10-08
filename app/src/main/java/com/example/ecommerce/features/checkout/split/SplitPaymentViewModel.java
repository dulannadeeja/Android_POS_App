package com.example.ecommerce.features.checkout.split;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.features.checkout.PaymentMethod;
import com.example.ecommerce.model.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class SplitPaymentViewModel extends ViewModel {
    private static final String TAG = "SplitPaymentViewModel";
    private MutableLiveData<Double> remainingAmount = new MutableLiveData<>(0.0);
    private MutableLiveData<Integer> splitCount = new MutableLiveData<>(1);
    private MutableLiveData<ArrayList<Payment>> splitPayments = new MutableLiveData<>(new ArrayList<>());
    private MutableLiveData<ArrayList<Integer>> userChangedPositions = new MutableLiveData<>(new ArrayList<>());

    public void init(double dueAmount) {
        remainingAmount.setValue(dueAmount);
        splitPayment();
    }

    public void splitPayment() {
        // Initialize an empty list to hold the split payments
        ArrayList<Payment> payments = new ArrayList<>();

        // Retrieve the remaining amount and split count values
        Double remainingAmountValue = remainingAmount.getValue();
        Integer splitCountValue = splitCount.getValue();

        // Check for null values in live data to avoid potential NullPointerExceptions
        if (remainingAmountValue == null || splitCountValue == null) {
            Log.e(TAG, "Invalid remaining amount or split count");
            return;
        }

        Log.d(TAG, "splitPayment: remaining amount: " + remainingAmountValue);
        Log.d(TAG, "splitPayment: split count: " + splitCountValue);

        // If the remaining amount is 0, reset the split payments to an empty list
        if (remainingAmountValue == 0.0) {
            Log.e(TAG, "splitPayment: remaining amount is 0");
            splitPayments.setValue(payments);
            return;
        }

        // If split count is 1, create a single payment with the full amount
        if (splitCountValue == 1) {
            Payment payment = new Payment.Builder()
                    .withPaymentAmount(remainingAmountValue)
                    .withPaymentMethod(PaymentMethod.CASH.getMethod())
                    .withIsPaid(false)
                    .build();
            payments.add(payment);
            splitPayments.setValue(payments);
            return;
        }

        // Calculate the payment amount for each split
        Double singlePaymentAmount = remainingAmountValue / splitCountValue;

        Log.d(TAG, "splitPayment: single payment amount: " + singlePaymentAmount);

        Double formattedSinglePaymentAmount = 0.0;

        // If single payment amount has more than 2 decimal places, truncate it to 2 decimal places without rounding
        if(getCountOfDecimals(singlePaymentAmount) > 2){
            Log.d(TAG, "splitPayment: single payment amount has more than 2 decimal places");
            String stringValue = String.valueOf(singlePaymentAmount);
            formattedSinglePaymentAmount = Double.parseDouble(stringValue.substring(0, stringValue.indexOf('.') + 3));
        }else{
            Log.d(TAG, "splitPayment: single payment amount has 2 or less decimal places");
            formattedSinglePaymentAmount = singlePaymentAmount;
        }

        Log.d(TAG, "splitPayment: formatted single payment amount: " + formattedSinglePaymentAmount);

        Double remaining = Double.parseDouble(String.format("%.2f", remainingAmountValue - (formattedSinglePaymentAmount * splitCountValue)));

        Log.d(TAG, "splitPayment: remaining: " + remaining);

        // Calculate the first payment amount and round it down to 2 decimal places
        Double firstPaymentAmount = Double.parseDouble(String.format("%.2f", new BigDecimal(formattedSinglePaymentAmount + remaining)
                .setScale(2, RoundingMode.DOWN)));

        Log.d(TAG, "splitPayment: first payment amount: " + firstPaymentAmount);

        // Generate split payments based on the split count
        for (int i = 0; i < splitCountValue; i++) {
            Payment payment = new Payment.Builder()
                    .withPaymentAmount(i == 0 ? firstPaymentAmount : formattedSinglePaymentAmount)
                    .withPaymentMethod(PaymentMethod.CASH.getMethod())
                    .withIsPaid(false)
                    .build();
            payments.add(payment);
        }

        // Update the split payments live data with the generated payments
        splitPayments.setValue(payments);
    }

    private int getCountOfDecimals(Double value) {
        String valueString = value.toString();
        if(!valueString.contains(".")) return 0;
        int decimalIndex = valueString.indexOf('.');
        return (valueString.length() - decimalIndex);
    }

    public void onAddSplitPayment() {
        splitCount.setValue(splitCount.getValue() + 1);
        Log.d(TAG, "onAddSplitPayment: split count: " + splitCount.getValue());
        splitPayment();
    }

    public void onRemoveSplitPayment() {
        if (splitCount.getValue() > 1) {
            splitCount.setValue(splitCount.getValue() - 1);
            splitPayment();
        }
    }

    public MutableLiveData<Double> getRemainingAmount() {
        return remainingAmount;
    }

    public MutableLiveData<Integer> getSplitCount() {
        return splitCount;
    }

    public MutableLiveData<ArrayList<Payment>> getSplitPayments() {
        return splitPayments;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount.setValue(remainingAmount);
    }

    public void setSplitCount(int splitCount) {
        this.splitCount.setValue(splitCount);
    }
}