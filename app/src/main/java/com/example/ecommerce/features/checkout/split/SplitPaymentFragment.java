package com.example.ecommerce.features.checkout.split;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentSplitPaymentBinding;
import com.example.ecommerce.features.checkout.CheckoutViewModel;
import com.example.ecommerce.utils.SplitPaymentsAdapter;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SplitPaymentFragment extends DialogFragment {

    private static final String TAG = "SplitPaymentFragment";
    private SplitPaymentViewModel splitPaymentViewModel;
    private CheckoutViewModel checkoutViewModel;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public static SplitPaymentFragment newInstance() {
        return new SplitPaymentFragment();
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
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_split_payment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentSplitPaymentBinding binding = FragmentSplitPaymentBinding.bind(view);

        splitPaymentViewModel = new ViewModelProvider(requireParentFragment()).get(SplitPaymentViewModel.class);
        checkoutViewModel = new ViewModelProvider(requireParentFragment(), App.appModule.provideCheckoutViewModelFactory()).get(CheckoutViewModel.class);

        splitPaymentViewModel.init(checkoutViewModel.getOrder().getValue().getDueAmount());

        // Set RecyclerView
        binding.splitPaymentRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        SplitPaymentsAdapter adapter = new SplitPaymentsAdapter(splitPaymentViewModel.getSplitPayments().getValue(),splitPaymentViewModel);
        Log.d("SplitPayment", "Adapter created");
        // Log payments
        splitPaymentViewModel.getSplitPayments().getValue().forEach(
                payment -> Log.d("SplitPayment", "Payment: " + payment.getPaymentAmount())
        );
        binding.splitPaymentRecyclerView.setAdapter(adapter);

        splitPaymentViewModel.getRemainingAmount().observe(getViewLifecycleOwner(), remainingAmount -> {
            binding.tvRemainingAmount.setText(String.valueOf(remainingAmount));
        });

        splitPaymentViewModel.getSplitCount().observe(getViewLifecycleOwner(), splitCount -> {
            binding.tvSplitCount.setText(String.valueOf(splitCount));
        });

        binding.addSplitPaymentButton.setOnClickListener(v -> {
            splitPaymentViewModel.onAddSplitPayment();
        });

        binding.removeSplitPaymentButton.setOnClickListener(v ->{
            splitPaymentViewModel.onRemoveSplitPayment();
        });

        splitPaymentViewModel.getSplitPayments().observe(getViewLifecycleOwner(), payments -> {
            compositeDisposable.add(
                    adapter.onDataSetChanged(payments)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(
                                    () -> {
                                        Log.d("SplitPayment", "Adapter has been updated successfully");
                                    },
                                    throwable -> {
                                        Log.e(TAG, "Error updating adapter", throwable);
                                    }
                            )
            );
        });
    }

}