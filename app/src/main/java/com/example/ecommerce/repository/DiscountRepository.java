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
import io.reactivex.rxjava3.core.Single;

public class DiscountRepository implements IDiscountRepository {
    private IDiscountDao discountDao;
    private SharedPreferences discountSharedPreferences;

    public DiscountRepository(IDiscountDao discountDao, SharedPreferences discountSharedPreferences) {
        this.discountDao = discountDao;
        this.discountSharedPreferences = discountSharedPreferences;
    }

    @Override
    public int newDiscountHandler(Discount discount) throws Exception {
        try {
            int discountId = 0;
            Discount existingDiscount = discountDao.isExistingDiscount(discount.getDiscountType(), discount.getDiscountValue());
            if (existingDiscount == null) {
                discountId = discountDao.createDiscount(discount);
            } else {
                discountId = existingDiscount.getDiscountId();
            }
            return discountId;
        } catch (Exception e) {
            throw new RuntimeException("Error adding new discount", e);
        }
    }

    @Override
    public void saveCurrentDiscount(Discount discount) throws Exception {
        try {
            SharedPreferences.Editor editor = discountSharedPreferences.edit();
            editor.putInt("discountId", discount.getDiscountId());
            editor.putString("discountType", discount.getDiscountType());
            editor.putFloat("discountValue", (float) discount.getDiscountValue());
            editor.apply();
            Log.d("DiscountRepository", "saveCurrentDiscount: discountId: " + discount.getDiscountId() + ", discountType: " + discount.getDiscountType() + ", discountValue: " + discount.getDiscountValue());
        } catch (Exception e) {
            throw new RuntimeException("Error saving current discount", e);
        }
    }

    @Override
    public Single<Discount> getCurrentDiscountHandler() {
        return Single.fromCallable(() -> {
            if (!isDiscountAvailable()) {
                return new Discount.DiscountBuilder().withDiscountId(-1).withDiscountType("").withDiscountValue(0).build();
            }

            // Extract discount data safely
            int discountId = discountSharedPreferences.getInt("discountId", -1);
            String discountType = discountSharedPreferences.getString("discountType", "");
            double discountValue = discountSharedPreferences.getFloat("discountValue", 0);

            Log.d("DiscountRepository", "getCurrentDiscountHandler: discountId: " + discountId + ", discountType: " + discountType + ", discountValue: " + discountValue);

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
    public void clearCurrentDiscount() throws Exception {
        try {
            SharedPreferences.Editor editor = discountSharedPreferences.edit();
            editor.clear();
            editor.apply();
        } catch (Exception e) {
            throw new RuntimeException("Error clearing current discount", e);
        }
    }

    @Override
    public Single<Map<String, Object>> applyCurrentDiscountHandler(double totalAmount) {
        return getCurrentDiscountHandler()
                .flatMap(discount -> {
                    Map<String, Object> result = new HashMap<>();
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
    public Discount getDiscountById(int discountId) throws Exception {
        try {
            return discountDao.getDiscount(discountId);
        } catch (Exception e) {
            throw new RuntimeException("Error getting discount by id", e);
        }
    }
}
