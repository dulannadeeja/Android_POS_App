package com.example.ecommerce.ui.customers.customer_profile;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentCustomerProfileBinding;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

public class CustomerProfileFragment extends DialogFragment {

    private CustomerProfileViewModel mViewModel;
    private Customer customer;
    private ICustomerRepository repository;

    public static CustomerProfileFragment newInstance(Customer customer) {
        CustomerProfileFragment fragment = new CustomerProfileFragment();
        fragment.customer = customer;
        fragment.repository = App.appModule.provideCustomerRepository();
        return fragment;
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
        return inflater.inflate(R.layout.fragment_customer_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCustomerProfileBinding binding = FragmentCustomerProfileBinding.bind(view);

        mViewModel = new ViewModelProvider(this, new CustomerProfileViewModelFactory(App.appModule.provideCustomerRepository())).get(CustomerProfileViewModel.class);

        mViewModel.setCustomer(customer);
        mViewModel.setIsCurrentCustomer(true);
        mViewModel.setTotalOutstandingBalance();

        // Observe view model data
        mViewModel.getCustomer().observe(getViewLifecycleOwner(), customer -> {
            if (customer != null) {
                binding.tvCustomerName.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
                if (customer.getPhoto() != null) {
                    binding.ivCustomerAvatar.setImageURI(Uri.parse(customer.getPhoto()));
                }
                binding.tvCustomerId.setText(String.valueOf(customer.getCustomerId()));
            }
        });

        mViewModel.getIsCurrentCustomer().observe(getViewLifecycleOwner(), isCurrentCustomer -> {
            if (isCurrentCustomer) {
                binding.tvRemoveFromReceipt.setText("REMOVE FROM RECEIPT");
                binding.tvRemoveFromReceipt.setOnClickListener(v -> {
                    repository.clearCurrentCustomerHandler();
                    dismiss();
                });
            } else {
                binding.tvRemoveFromReceipt.setText("ADD TO RECEIPT");
                binding.tvRemoveFromReceipt.setOnClickListener(v -> {
                            repository.setCurrentCustomerHandler(customer.getCustomerId());
                            dismiss();
                        }
                );
            }
        });

        mViewModel.getTotalOutstandingBalance().observe(getViewLifecycleOwner(), totalOutstandingBalance -> {
            binding.tvCustomerOutstanding.setText(String.valueOf(totalOutstandingBalance));
        });


        binding.ivBack.setOnClickListener(v -> dismiss());
    }

}