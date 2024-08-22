package com.example.ecommerce.ui.discount;

import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Discount;
import com.example.ecommerce.repository.IDiscountRepository;

public class DiscountViewModel extends ViewModel {
    private IDiscountRepository discountRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private MutableLiveData<Discount> discount = new MutableLiveData<>();
    private MutableLiveData<String> discountType = new MutableLiveData<>("");
    private MutableLiveData<Double> discountValue = new MutableLiveData<>(0.0);

    public DiscountViewModel(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
        setDiscount();
        Log.d("DiscountViewModel", "DiscountViewModel created");
    }

    public void setDiscount() {
        try {
            isLoading.setValue(true);
            Discount currentDiscount = discountRepository.getCurrentDiscount();
            Log.d("DiscountViewModel", "Current discount: " + currentDiscount);
            if(currentDiscount != null) {
                discount.setValue(currentDiscount);
                discountType.setValue(currentDiscount.getDiscountType());
                discountValue.setValue(currentDiscount.getDiscountValue());
            } else {
                discount.setValue(null);
                discountType.setValue("");
                discountValue.setValue(0.0);
            }
            errorMessage.setValue("");
        } catch (Exception e) {
            errorMessage.setValue("Error fetching discount");
            Log.e("DiscountViewModel", "Error fetching discount", e);
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onAddDiscount(Discount discount) {
        try {
            isLoading.setValue(true);
            discountRepository.addNewDiscount(discount);
            setDiscount();
            errorMessage.setValue("");
        } catch (Exception e) {
            errorMessage.setValue("Error adding discount");
            Log.e("DiscountViewModel", "Error adding discount", e);
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onClearDiscount() {
        try {
            isLoading.setValue(true);
            discountRepository.clearDiscount();
            setDiscount();
            errorMessage.setValue("");
        } catch (Exception e) {
            errorMessage.setValue("Error clearing discount");
            Log.e("DiscountViewModel", "Error clearing discount", e);
        } finally {
            isLoading.setValue(false);
        }
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public MutableLiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public MutableLiveData<Discount> getDiscount() {
        return discount;
    }

    public MutableLiveData<String> getDiscountType() {
        return discountType;
    }

    public MutableLiveData<Double> getDiscountValue() {
        return discountValue;
    }

    public void setDiscountType(String discountType) {
        this.discountType.setValue(discountType);
    }

    public void setDiscountValue(Double discountValue) {
        this.discountValue.setValue(discountValue);
    }

}
