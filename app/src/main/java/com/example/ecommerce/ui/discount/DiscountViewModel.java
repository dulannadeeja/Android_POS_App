package com.example.ecommerce.ui.discount;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Discount;
import com.example.ecommerce.repository.IDiscountRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DiscountViewModel extends ViewModel {
    private IDiscountRepository discountRepository;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private MutableLiveData<Discount> discount = new MutableLiveData<>();
    private MutableLiveData<String> discountType = new MutableLiveData<>("");
    private MutableLiveData<Double> discountValue = new MutableLiveData<>(0.0);
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public DiscountViewModel(IDiscountRepository discountRepository) {
        this.discountRepository = discountRepository;
        onFetchCurrentDiscount();
    }

    public void onFetchCurrentDiscount() {
        isLoading.setValue(true);
        compositeDisposable.add(
                discountRepository.getCurrentDiscountHandler()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(discount -> {
                                this.discount.postValue(discount);
                                this.discountType.postValue(discount.getDiscountType());
                                this.discountValue.postValue(discount.getDiscountValue());
                            errorMessage.postValue("");
                        }, throwable -> {
                            this.discount.postValue(null);
                            this.discountType.postValue("");
                            this.discountValue.postValue(0.0);
                            errorMessage.postValue("Error fetching discount");
                            Log.e("DiscountViewModel", "Error fetching discount", throwable);
                        })
        );
        isLoading.setValue(false);
        errorMessage.setValue("");
    }

    public void onSetCurrentDiscount(int discountId) {
        try {
            isLoading.setValue(true);
            Discount discount = discountRepository.getDiscountById(discountId);
            discountRepository.saveCurrentDiscount(discount);
            onFetchCurrentDiscount();
            errorMessage.setValue("");
        } catch (Exception e) {
            errorMessage.setValue("Error setting discount");
            Log.e("DiscountViewModel", "Error setting discount", e);
        } finally {
            isLoading.setValue(false);
        }
    }

    public void onAddDiscount(Discount discount) {
        try {
            isLoading.setValue(true);
            int discountId = discountRepository.newDiscountHandler(discount);
            Discount discountWithId = new Discount.DiscountBuilder()
                    .withDiscountId(discountId)
                    .withDiscountType(discount.getDiscountType())
                    .withDiscountValue(discount.getDiscountValue())
                    .build();
            discountRepository.saveCurrentDiscount(discountWithId);
            onFetchCurrentDiscount();
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
            discountRepository.clearCurrentDiscount();
            onFetchCurrentDiscount();
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
