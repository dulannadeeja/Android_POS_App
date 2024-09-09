package com.example.ecommerce.ui.customers.create_customer;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Customer;
import com.example.ecommerce.repository.ICustomerRepository;
import com.example.ecommerce.ui.products.ProductsFragment;

public class CreateCustomerViewModel extends ViewModel {
    private ICustomerRepository customerRepository;
    private MutableLiveData<Customer> customer = new MutableLiveData<>(new Customer.CustomerBuilder().buildCustomer());
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

    public void loadCurrentCustomer() {
        Customer currentCustomer = customerRepository.getCurrentCustomerHandler();
        currentCustomer = currentCustomer != null ? currentCustomer : new Customer.CustomerBuilder().buildCustomer();
        customer.setValue(currentCustomer);
    }

    public Boolean validateFirstName() {
        String firstName = customer.getValue().getFirstName() == null ? "" : customer.getValue().getFirstName();
        if (firstName.isEmpty()) {
            firstNameError.setValue("Looks like you forgot to enter your first name!");
            return false;
        }
        firstNameError.setValue(null);
        return true;
    }

    public Boolean validateLastName() {
        String lastName = customer.getValue().getLastName() == null ? "" : customer.getValue().getLastName();
        if (lastName.isEmpty()) {
            lastNameError.setValue("Looks like you forgot to enter your last name!");
            return false;
        }
        lastNameError.setValue(null);
        return true;
    }

    public Boolean validateEmail() {
        String email = customer.getValue().getEmail() == null ? "" : customer.getValue().getEmail();
        if (!email.isEmpty()) {
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                emailError.setValue("Please enter a valid email address!");
                return false;
            }
        }
        emailError.setValue(null);
        return true;
    }

    public Boolean validatePhone() {
        String phone = customer.getValue().getPhone() == null ? "" : customer.getValue().getPhone();
        if (!phone.isEmpty()) {
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

    public Boolean validateAddress(){
        String address = customer.getValue().getAddress() == null ? "" : customer.getValue().getAddress();
        if (address.isEmpty()) {
            addressError.setValue("Looks like you forgot to enter the address!");
            return false;
        }
        addressError.setValue(null);
        return true;
    }

    public Boolean validateCity(){
        String city = customer.getValue().getCity() == null ? "" : customer.getValue().getCity();
        if (city.isEmpty()) {
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

    public Boolean validateRegion(){
        String region = customer.getValue().getRegion() == null ? "" : customer.getValue().getRegion();
        if (region.isEmpty()) {
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

    public Boolean validateGender() {
        String gender = customer.getValue().getGender() == null ? "" : customer.getValue().getGender();
        if (gender.isEmpty()) {
            genderError.setValue("Looks like you forgot to enter the gender!");
            return false;
        }
        genderError.setValue(null);
        return true;
    }

    public Boolean validateCustomer() {
        Boolean isValid = false;
        isValid = validateFirstName();
        isValid = validateLastName() && isValid;
        isValid = validateEmail() && isValid;
        isValid = validatePhone() && isValid;
        isValid = validateGender() && isValid;
        Boolean isAddressNull = customer.getValue().getAddress() == null || customer.getValue().getAddress().isEmpty();
        Boolean isCityNull = customer.getValue().getCity() == null || customer.getValue().getCity().isEmpty();
        Boolean isRegionNull = customer.getValue().getRegion() == null || customer.getValue().getRegion().isEmpty();
        if(!isAddressNull || !isCityNull || !isRegionNull){
            isValid = validateAddress() && isValid;
            isValid = validateCity() && isValid;
            isValid = validateRegion() && isValid;
        }
        return isValid;
    }

    public void onConfirmCustomerSave(OnConfirmCustomerCallback onConfirmCustomerCallback) {
        try {
            if (validateCustomer()) {
                int customerId = customerRepository.newCustomerHandler(customer.getValue());
                customerRepository.setCurrentCustomerHandler(customerId);
                onConfirmCustomerCallback.onSuccessfulCustomerCreation();
                ProductsFragment.markAddCustomerIconAsActive();
            }
        } catch (Exception e) {
            onConfirmCustomerCallback.onFailedCustomerCreation();
        }
    }

    public MutableLiveData<Customer> getCustomer() {
        return customer;
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

    public void clearGender() {
        customer.getValue().setGender(null);
    }

    public <T> void applyUpdateToCustomer(String field, T value) {
        Customer customerClone = customer.getValue();
        switch (field) {
            case "firstName":
                customerClone.setFirstName((String) value);
                validateFirstName();
                break;
            case "lastName":
                customerClone.setLastName((String) value);
                validateLastName();
                break;
            case "email":
                customerClone.setEmail((String) value);
                validateEmail();
                break;
            case "phone":
                customerClone.setPhone((String) value);
                validatePhone();
                break;
            case "address":
                customerClone.setAddress((String) value);
                break;
            case "city":
                customerClone.setCity((String) value);
                break;
            case "region":
                customerClone.setRegion((String) value);
                break;
            case "gender":
                customerClone.setGender((String) value);
                validateGender();
                break;
            case "photo":
                customerClone.setPhoto((String) value);
                break;
            default:
                throw new IllegalArgumentException("Invalid field name" + field);

        }

        customer.setValue(customerClone);
    }

    public void clearCustomer() {
        customer.setValue(new Customer.CustomerBuilder().buildCustomer());
        firstNameError.setValue(null);
        lastNameError.setValue(null);
        emailError.setValue(null);
        phoneError.setValue(null);
        addressError.setValue(null);
        cityError.setValue(null);
        regionError.setValue(null);
        genderError.setValue(null);
        customerRepository.clearCurrentCustomerHandler();
    }

}
