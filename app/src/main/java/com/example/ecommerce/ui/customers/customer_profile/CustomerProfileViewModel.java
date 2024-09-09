package com.example.ecommerce.ui.customers.customer_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

public class CustomerProfileViewModel extends ViewModel {
    private final ICustomerRepository customerRepository;
    private MutableLiveData<Customer> customer = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCurrentCustomer = new MutableLiveData<>();
    private MutableLiveData<Double> totalOutstandingBalance = new MutableLiveData<>();

    public CustomerProfileViewModel(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public void setCustomer(Customer customer) {
        if (customer != null) {
            this.customer.setValue(customer);
        } else {
            this.customer.setValue(new Customer.CustomerBuilder().buildCustomer());
        }
    }

    public void setIsCurrentCustomer(boolean isCurrentCustomer) {
        Customer currentCustomer = customerRepository.getCurrentCustomerHandler();
        if (currentCustomer != null && currentCustomer.getCustomerId() == customer.getValue().getCustomerId()) {
            this.isCurrentCustomer.setValue(true);
        } else {
            this.isCurrentCustomer.setValue(false);
        }
    }

    public void setTotalOutstandingBalance() {
        double totalOutstandingBalance = customerRepository.getCustomerOutstandingBalanceHandler(customer.getValue().getCustomerId());
        this.totalOutstandingBalance.setValue(totalOutstandingBalance);
    }

    public MutableLiveData<Customer> getCustomer() {
        return customer;
    }

    public MutableLiveData<Boolean> getIsCurrentCustomer() {
        return isCurrentCustomer;
    }

    public MutableLiveData<Double> getTotalOutstandingBalance() {
        return totalOutstandingBalance;
    }
}