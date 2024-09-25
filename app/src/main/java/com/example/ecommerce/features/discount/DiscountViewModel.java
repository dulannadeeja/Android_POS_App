package com.example.ecommerce.features.discount;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.ecommerce.model.Discount;
import com.example.ecommerce.repository.IDiscountRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class DiscountViewModel extends ViewModel {
    private static final String TAG = "DiscountViewModel";
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
                            this.discount.setValue(discount);
                            this.discountType.setValue(discount.getDiscountType());
                            this.discountValue.setValue(discount.getDiscountValue());
                            errorMessage.setValue("");
                        }, throwable -> {
                            this.discount.setValue(null);
                            this.discountType.setValue("");
                            this.discountValue.setValue(0.0);
                            errorMessage.setValue("Error fetching discount");
                            Log.e(TAG, "Error fetching discount", throwable);
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
            Log.e(TAG, "Error setting discount", e);
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
            Log.e(TAG, "Error adding discount", e);
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
            Log.e(TAG, "Error clearing discount", e);
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
