package com.example.ecommerce.ui.customers;

import android.widget.Toast;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;
import java.util.ArrayList;

public class CustomersViewModel extends ViewModel {
    private final ICustomerRepository repository;

    private MutableLiveData<ArrayList<Customer>> customers = new MutableLiveData<>();

    public CustomersViewModel(ICustomerRepository repository) {
        this.repository = repository;
        customers.setValue(repository.getAllCustomersHandler());
    }

    public MutableLiveData<ArrayList<Customer>> getCustomers() {
        return customers;
    }
}