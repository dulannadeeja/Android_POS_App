package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.utils.RoomDBHelper;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * Repository responsible for managing discount-related data operations,
 * interacting with Room DB and SharedPreferences for persistent storage.
 */
public class DiscountRepository implements IDiscountRepository {

    private static final String TAG = "DiscountRepository";

    private final IDiscountDao discountDao;
    private final SharedPreferences discountSharedPreferences;

    /**
     * Constructor for DiscountRepository, initializing DAO and SharedPreferences.
     *
     * @param database The RoomDBHelper instance to get DAO access.
     * @param discountSharedPreferences SharedPreferences to store and retrieve current discount.
     */
    public DiscountRepository(RoomDBHelper database, SharedPreferences discountSharedPreferences) {
        this.discountDao = database.discountDao();
        this.discountSharedPreferences = discountSharedPreferences;
    }

    /**
     * Handles adding a new discount or returning an existing one if the same discount already exists.
     *
     * @param discount The Discount object to add or check for existence.
     * @return Single emitting the ID of the discount.
     */
    @Override
    public Single<Integer> newDiscountHandler(Discount discount) {
        return discountDao.isExistingDiscount(discount.getDiscountType(), discount.getDiscountValue())
                .flatMapSingle(existingDiscount -> {
                    Log.d(TAG, "Discount already exists with ID: " + existingDiscount.getDiscountId());
                    return Single.just(existingDiscount.getDiscountId());
                })
                .switchIfEmpty(
                        discountDao.createDiscount(discount)
                                .flatMap(newDiscountId -> {
                                    Log.d(TAG, "New discount created with ID: " + newDiscountId);
                                    return Single.just(newDiscountId.intValue());
                                })
                );
    }

    /**
     * Saves the current discount to SharedPreferences for easy access later.
     *
     * @param discount The Discount object to be saved.
     * @return Completable representing the success or failure of the operation.
     */
    @Override
    public Completable saveCurrentDiscount(Discount discount) {
        return Completable.fromAction(() -> {
            try {
                Log.d(TAG, "Saving current discount: " + discount.getDiscountType() + " with value: " + discount.getDiscountValue());
                SharedPreferences.Editor editor = discountSharedPreferences.edit();
                editor.putInt("discountId", discount.getDiscountId());
                editor.putString("discountType", discount.getDiscountType());
                editor.putFloat("discountValue", (float) discount.getDiscountValue());
                editor.apply();
            } catch (Exception e) {
                Log.e(TAG, "Error saving current discount", e);
                throw new RuntimeException("Error saving current discount", e);
            }
        });
    }

    /**
     * Retrieves the current discount stored in SharedPreferences.
     *
     * @return Single emitting the Discount object or a default discount if none is found.
     */
    @Override
    public Single<Discount> getCurrentDiscountHandler() {
        return Single.fromCallable(() -> {
            if (!isDiscountAvailable()) {
                Log.d(TAG, "No discount available in SharedPreferences");
                return new Discount.DiscountBuilder().withDiscountId(-1).withDiscountType("").withDiscountValue(0).build();
            }

            // Retrieve discount details from SharedPreferences
            int discountId = discountSharedPreferences.getInt("discountId", -1);
            String discountType = discountSharedPreferences.getString("discountType", "");
            double discountValue = discountSharedPreferences.getFloat("discountValue", 0);

            Log.d(TAG, "Current discount retrieved with ID: " + discountId);

            return new Discount.DiscountBuilder()
                    .withDiscountId(discountId)
                    .withDiscountType(discountType)
                    .withDiscountValue(discountValue)
                    .build();
        });
    }

    /**
     * Checks if a discount is currently available in SharedPreferences.
     *
     * @return True if a discount is available, otherwise false.
     */
    private boolean isDiscountAvailable() {
        return discountSharedPreferences.contains("discountId") &&
                discountSharedPreferences.contains("discountType") &&
                discountSharedPreferences.contains("discountValue");
    }

    /**
     * Clears the current discount from SharedPreferences.
     *
     * @return Completable representing the success or failure of the operation.
     */
    @Override
    public Completable clearCurrentDiscount() {
        return Completable.fromAction(() -> {
            try {
                Log.d(TAG, "Clearing current discount from SharedPreferences");
                SharedPreferences.Editor editor = discountSharedPreferences.edit();
                editor.clear();
                editor.apply();
            } catch (Exception e) {
                Log.e(TAG, "Error clearing current discount", e);
                throw new RuntimeException("Error clearing current discount", e);
            }
        });
    }

    /**
     * Applies the current discount to a given total amount and calculates the discounted amount.
     *
     * @param totalAmount The original total amount before discount.
     * @return Single emitting a Map containing the discount amount and discount ID.
     */
    @Override
    public Single<Map<String, Object>> applyCurrentDiscountHandler(double totalAmount) {
        return getCurrentDiscountHandler()
                .flatMap(discount -> {
                    Map<String, Object> result = new HashMap<>();

                    if (totalAmount == 0) {
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

    /**
     * Retrieves a discount by its ID from the Room database.
     *
     * @param discountId The ID of the discount to retrieve.
     * @return Maybe emitting the Discount object if found, or completes if not found.
     */
    @Override
    public Maybe<Discount> getDiscountById(int discountId) {
        try {
            Log.d(TAG, "Fetching discount with ID: " + discountId);
            return discountDao.getDiscount(discountId);
        } catch (Exception e) {
            Log.e(TAG, "Error getting discount by ID", e);
            throw new RuntimeException("Error getting discount by ID", e);
        }
    }
}
