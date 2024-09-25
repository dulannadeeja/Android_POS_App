package com.example.ecommerce.features.customers.create_customer;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;

public class CreateCustomerViewModel extends ViewModel {
    public static final String TAG = "CreateCustomerViewModel";
    private ICustomerRepository customerRepository;

    private MutableLiveData<Boolean> isEditingMode = new MutableLiveData<>(false);
    private MutableLiveData<String> firstNameError = new MutableLiveData<>();
    private MutableLiveData<String> lastNameError = new MutableLiveData<>();
    private MutableLiveData<String> emailError = new MutableLiveData<>();
    private MutableLiveData<String> phoneError = new MutableLiveData<>();
    private MutableLiveData<String> addressError = new MutableLiveData<>();
    private MutableLiveData<String> cityError = new MutableLiveData<>();
    private MutableLiveData<String> regionError = new MutableLiveData<>();
    private MutableLiveData<String> genderError = new MutableLiveData<>();

    public CreateCustomerViewModel(ICustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Boolean validateFirstName(String firstName) {
        if (firstName == null || firstName.isEmpty()) {
            firstNameError.setValue("Looks like you forgot to enter your first name!");
            return false;
        }
        firstNameError.setValue(null);
        return true;
    }

    public Boolean validateLastName(String lastName) {
        if (lastName == null || lastName.isEmpty()) {
            lastNameError.setValue("Looks like you forgot to enter your last name!");
            return false;
        }
        lastNameError.setValue(null);
        return true;
    }

    public Boolean validateEmail(String email) {
        if (email != null && !email.isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.setValue("Please enter a valid email address!");
                return false;
            }
        }
        emailError.setValue(null);
        return true;
    }

    public Boolean validatePhone(String phone) {
        if (phone != null && !phone.isEmpty()) {
            if (!android.util.Patterns.PHONE.matcher(phone).matches()) {
                phoneError.setValue("Please enter a valid phone number!");
                return false;
            }
            if (phone.length() != 10) {
                phoneError.setValue("Phone number must be 10 digits long!");
                return false;
            }
        }else{
            phoneError.setValue("Looks like you forgot to enter the phone number!");
            return false;
        }
        phoneError.setValue(null);
        return true;
    }

    public Boolean validateAddress(String address) {
        if (address == null || address.isEmpty()) {
            addressError.setValue("Looks like you forgot to enter the address!");
            return false;
        }
        addressError.setValue(null);
        return true;
    }

    public Boolean validateCity(String city) {
        if (city == null || city.isEmpty()) {
            cityError.setValue("Looks like you forgot to enter the city!");
            return false;
        }
        // city cannot contain numbers, special characters
        if (!city.matches("^[a-zA-Z]*$")) {
            cityError.setValue("City cannot contain numbers or special characters!");
            return false;
        }
        // city cannot be longer than 50 characters
        if (city.length() > 50) {
            cityError.setValue("City cannot be longer than 50 characters!");
            return false;
        }
        // city cannot be shorter than 2 characters
        if (city.length() < 2) {
            cityError.setValue("City cannot be shorter than 2 characters!");
            return false;
        }
        cityError.setValue(null);
        return true;
    }

    public Boolean validateRegion(String region) {
        if (region == null || region.isEmpty()) {
            regionError.setValue("Looks like you forgot to enter the region!");
            return false;
        }
        // region cannot contain numbers, special characters
        if (!region.matches("^[a-zA-Z]*$")) {
            regionError.setValue("Region cannot contain numbers or special characters!");
            return false;
        }
        // region cannot be longer than 50 characters
        if (region.length() > 50) {
            regionError.setValue("Region cannot be longer than 50 characters!");
            return false;
        }
        // region cannot be shorter than 2 characters
        if (region.length() < 2) {
            regionError.setValue("Region cannot be shorter than 2 characters!");
            return false;
        }
        regionError.setValue(null);
        return true;
    }

    public Boolean validateGender(String gender) {
        if (gender != null && gender.isEmpty()) {
            genderError.setValue("Looks like you forgot to enter the gender!");
            return false;
        }
        genderError.setValue(null);
        return true;
    }

    public Boolean validateCustomer(Customer customer) {
        Boolean isValid = false;
        isValid = validateFirstName(customer.getFirstName());
        isValid = validateLastName(customer.getLastName()) && isValid;
        isValid = validateEmail(customer.getEmail()) && isValid;
        isValid = validatePhone(customer.getPhone()) && isValid;
        isValid = validateGender(customer.getGender()) && isValid;
        Boolean isAddressNull = customer.getAddress() == null || customer.getAddress().isEmpty();
        Boolean isCityNull = customer.getCity() == null || customer.getCity().isEmpty();
        Boolean isRegionNull = customer.getRegion() == null || customer.getRegion().isEmpty();
        if(!isAddressNull || !isCityNull || !isRegionNull){
            isValid = validateAddress(customer.getAddress()) && isValid;
            isValid = validateCity(customer.getCity()) && isValid;
            isValid = validateRegion(customer.getRegion()) && isValid;
        }
        return isValid;
    }

    public void onConfirmCustomerSave(Customer customer,OnConfirmCustomerCallback onConfirmCustomerCallback) {
        try {
            if (validateCustomer(customer)) {
                int customerId = customerRepository.newCustomerHandler(customer);
                customerRepository.setCurrentCustomerHandler(customerId);
                onConfirmCustomerCallback.onSuccessful();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while saving customer, ", e);
            onConfirmCustomerCallback.onFailed();
        }
    }

    public void onConfirmCustomerUpdate(Customer customer,OnConfirmCustomerCallback onConfirmCustomerCallback) {
        try {
            if (validateCustomer(customer)) {
                customerRepository.updateCustomerHandler(customer);
                onConfirmCustomerCallback.onSuccessful();
            }
        } catch (Exception e) {
            Log.e(TAG, "error while updating customer, ", e);
            onConfirmCustomerCallback.onFailed();
        }
    }

    public void onClearCustomerErrors() {
        firstNameError.setValue(null);
        lastNameError.setValue(null);
        emailError.setValue(null);
        phoneError.setValue(null);
        addressError.setValue(null);
        cityError.setValue(null);
        regionError.setValue(null);
        genderError.setValue(null);
    }

    public MutableLiveData<String> getFirstNameError() {
        return firstNameError;
    }

    public MutableLiveData<String> getLastNameError() {
        return lastNameError;
    }

    public MutableLiveData<String> getEmailError() {
        return emailError;
    }

    public MutableLiveData<String> getPhoneError() {
        return phoneError;
    }

    public MutableLiveData<String> getGenderError() {
        return genderError;
    }

    public MutableLiveData<String> getAddressError() {
        return addressError;
    }

    public MutableLiveData<String> getCityError() {
        return cityError;
    }

    public MutableLiveData<String> getRegionError() {
        return regionError;
    }

    public MutableLiveData<Boolean> getIsEditingMode() {
        return isEditingMode;
    }

    public void setIsEditingMode(Boolean isEditingMode) {
        this.isEditingMode.setValue(isEditingMode);
    }

}
