package com.example.ecommerce.features.customers.customer_profile;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Scheduler;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class CustomerProfileViewModel extends ViewModel {
    private final ICustomerRepository customerRepository;
    private MutableLiveData<Customer> customer = new MutableLiveData<>();
    private MutableLiveData<Boolean> isCurrentCustomer = new MutableLiveData<>();
    private MutableLiveData<Double> totalOutstandingBalance = new MutableLiveData<>();
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

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

    public void setIsCurrentCustomer() {
        compositeDisposable.add(
                customerRepository.getCurrentCustomerHandler()
                        .subscribe(currentCustomer -> {
                            if (currentCustomer != null && currentCustomer.getCustomerId() == customer.getValue().getCustomerId()) {
                                this.isCurrentCustomer.setValue(true);
                            } else {
                                this.isCurrentCustomer.setValue(false);
                            }
                        }, throwable -> {
                            this.isCurrentCustomer.setValue(false);
                        }, () -> {
                            this.isCurrentCustomer.setValue(false);
                        })
        );
    }

    public void setTotalOutstandingBalance() {
        compositeDisposable.add(
                customerRepository.getCustomerOutstandingBalanceHandler(Objects.requireNonNull(customer.getValue()).getCustomerId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(totalOutstandingBalance -> {
                            this.totalOutstandingBalance.setValue(totalOutstandingBalance);
                        }, throwable -> {
                            this.totalOutstandingBalance.setValue(0.0);
                        })
        );
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