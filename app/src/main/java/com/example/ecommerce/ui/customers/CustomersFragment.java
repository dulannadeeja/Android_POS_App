package com.example.ecommerce.ui.customers;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentCustomersBinding;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.ui.customers.create_customer.CreateCustomerFragment;
import com.example.ecommerce.ui.customers.customer_profile.CustomerProfileFragment;
import com.example.ecommerce.utils.CustomersAdapter;

import java.util.ArrayList;

public class CustomersFragment extends DialogFragment implements OnCustomerClickListener{

    public static final String TAG = "CustomersFragment";
    private CustomersViewModel customersViewModel;
    private CustomerViewModel customerViewModel;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                dismiss();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_customers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCustomersBinding binding = FragmentCustomersBinding.bind(view);

        customersViewModel = new ViewModelProvider(this,new CustomersViewModelFactory(App.appModule.provideCustomerRepository())).get(CustomersViewModel.class);
        customerViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCustomerViewModelFactory()).get(CustomerViewModel.class);


        // Set up the RecyclerView
        binding.customersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        CustomersAdapter adapter = new CustomersAdapter(new ArrayList<Customer>(), this);
        binding.customersRecyclerView.setAdapter(adapter);

        binding.backButton.setOnClickListener(v -> dismiss());

        binding.addNewCustomerButton.setOnClickListener(v -> {
            CreateCustomerFragment createCustomerFragment = new CreateCustomerFragment();
            createCustomerFragment.show(getParentFragmentManager(), "CreateCustomerFragment");
        });

        // Observe the customers
        customersViewModel.getCustomers().observe(getViewLifecycleOwner(), adapter::setCustomers);
    }


    @Override
    public void onAddCustomerClicked(Customer customer) {
        Toast.makeText(getContext(), "Add Customer Clicked", Toast.LENGTH_SHORT).show();
        customerViewModel.onSetCurrentCustomer(customer.getCustomerId());
    }

    @Override
    public void onCustomerClick(Customer customer) {
        CustomerProfileFragment customerProfileFragment = CustomerProfileFragment.newInstance(customer);
        customerProfileFragment.show(getParentFragmentManager(), "CustomerProfileFragment");
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}