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
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>(false);
    private MutableLiveData<String> errorMessage = new MutableLiveData<>("");
    private MutableLiveData<Discount> discount = new MutableLiveData<>();
    private MutableLiveData<String> discountType = new MutableLiveData<>("");
    private MutableLiveData<Double> discountValue = new MutableLiveData<>(0.0);

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
        isLoading.setValue(true);

        compositeDisposable.add(
                discountRepository.getDiscountById(discountId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMapCompletable(discount ->
                                discountRepository.saveCurrentDiscount(discount)
                        )
                        .doOnComplete(this::onFetchCurrentDiscount)  // Fetch current discount once saved
                        .subscribe(
                                () -> {
                                    Log.d(TAG, "Discount set successfully");
                                    errorMessage.setValue("");
                                    isLoading.setValue(false);
                                },
                                throwable -> {
                                    errorMessage.setValue("Error setting discount");
                                    Log.e(TAG, "Error setting discount", throwable);
                                    isLoading.setValue(false);
                                }
                        )
        );
    }

    public void onAddDiscount(Discount discount) {
        isLoading.setValue(true);
        Log.d(TAG, "Adding discount started");

        compositeDisposable.add(
                discountRepository.newDiscountHandler(discount)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .flatMap(discountId -> {
                            Discount discountWithId = new Discount.DiscountBuilder()
                                    .withDiscountId(discountId)
                                    .withDiscountType(discount.getDiscountType())
                                    .withDiscountValue(discount.getDiscountValue())
                                    .build();

                            // Return the saveCurrentDiscount call as a Single to chain it.
                            return discountRepository.saveCurrentDiscount(discountWithId)
                                    .toSingleDefault(discountId);
                        })
                        .subscribe(
                                discountId -> {
                                    onFetchCurrentDiscount(); // Fetch the current discount after successful addition.
                                    Log.d(TAG, "Discount added and saved successfully");
                                    errorMessage.setValue(""); // Clear error messages if successful.
                                },
                                throwable -> {
                                    errorMessage.setValue("Error adding or saving discount");
                                    Log.e(TAG, "Error adding or saving discount", throwable);
                                }
                        )
        );
    }

    public void onClearDiscount() {
        isLoading.setValue(true);
        compositeDisposable.add(
                discountRepository.clearCurrentDiscount()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    Log.d(TAG, "Discount cleared successfully");
                                    errorMessage.setValue("");
                                    onFetchCurrentDiscount(); // Fetch the current discount after successful clearing.
                                },
                                throwable -> {
                                    errorMessage.setValue("Error clearing discount");
                                    Log.e(TAG, "Error clearing discount", throwable);
                                    isLoading.setValue(false);
                                }
                        )
        );
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
