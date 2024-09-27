package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.App;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.model.Discount;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

public class DiscountRepository implements IDiscountRepository {
    private IDiscountDao discountDao;
    private SharedPreferences discountSharedPreferences;

    public DiscountRepository(IDiscountDao discountDao, SharedPreferences discountSharedPreferences) {
        this.discountDao = discountDao;
        this.discountSharedPreferences = discountSharedPreferences;
    }

    @Override
    public Single<Integer> newDiscountHandler(Discount discount) {
        return discountDao.isExistingDiscount(discount.getDiscountType(), discount.getDiscountValue())
                .flatMapSingle(existingDiscount -> {
                            Log.d("DiscountRepo", "Discount already exists with ID: " + existingDiscount.getDiscountId());
                            return Single.just(existingDiscount.getDiscountId()); // If a discount exists, return its ID.
                        }
                )
                .switchIfEmpty(
                        discountDao.createDiscount(discount) // If no discount exists, create it and return the ID.
                );
    }

    @Override
    public Completable saveCurrentDiscount(Discount discount) {
        return Completable.fromAction(()->{
            try {
                Log.d("DiscountRepo", "Saving current discount: " + discount.getDiscountType() + " with value: " + discount.getDiscountValue());
                SharedPreferences.Editor editor = discountSharedPreferences.edit();
                editor.putInt("discountId", discount.getDiscountId());
                editor.putString("discountType", discount.getDiscountType());
                editor.putFloat("discountValue", (float) discount.getDiscountValue());
                editor.apply();
            } catch (Exception e) {
                throw new RuntimeException("Error saving current discount", e);
            }
        });
    }

    @Override
    public Single<Discount> getCurrentDiscountHandler() {
        return Single.fromCallable(() -> {
            if (!isDiscountAvailable()) {
                Log.d("DiscountRepo", "No discount available");
                return new Discount.DiscountBuilder().withDiscountId(-1).withDiscountType("").withDiscountValue(0).build();
            }

            // Extract discount data safely
            int discountId = discountSharedPreferences.getInt("discountId", -1);
            String discountType = discountSharedPreferences.getString("discountType", "");
            double discountValue = discountSharedPreferences.getFloat("discountValue", 0);

            Log.d("DiscountRepo", "Current discount available with ID: " + discountId);

            // Build and return the Discount object
            return new Discount.DiscountBuilder()
                    .withDiscountId(discountId)
                    .withDiscountType(discountType)
                    .withDiscountValue(discountValue)
                    .build();
        });
    }

    private boolean isDiscountAvailable() {
        // Check if all required keys exist
        return discountSharedPreferences.contains("discountId") &&
                discountSharedPreferences.contains("discountType") &&
                discountSharedPreferences.contains("discountValue");
    }

    @Override
    public Completable clearCurrentDiscount() {
        return Completable.fromAction(() ->{
            try {
                SharedPreferences.Editor editor = discountSharedPreferences.edit();
                editor.clear();
                editor.apply();
            } catch (Exception e) {
                throw new RuntimeException("Error clearing current discount", e);
            }
        });
    }

    @Override
    public Single<Map<String, Object>> applyCurrentDiscountHandler(double totalAmount) {
        return getCurrentDiscountHandler()
                .flatMap(discount -> {
                    Map<String, Object> result = new HashMap<>();

                    if(totalAmount == 0) {
                        result.put("discountAmount", 0);
                        result.put("discountId", -1);
                        return Single.just(result);
                    }

                    double discountAmount;
                    switch (discount.getDiscountType()) {
                        case "percentage":
                            discountAmount = totalAmount * discount.getDiscountValue() / 100;
                            break;
                        case "fixed":
                            discountAmount = discount.getDiscountValue();
                            break;
                        default:
                            result.put("discountAmount", 0);
                            result.put("discountId", -1);
                            return Single.just(result);
                    }

                    result.put("discountAmount", discountAmount);
                    result.put("discountId", discount.getDiscountId());
                    return Single.just(result);
                });
    }

    @Override
    public Maybe<Discount> getDiscountById(int discountId) {
        try {
            return discountDao.getDiscount(discountId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting discount by id", e);
        }
    }
}
