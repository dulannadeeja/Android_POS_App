package com.example.ecommerce.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.ecommerce.model.Discount;

import io.reactivex.rxjava3.core.Maybe;
import io.reactivex.rxjava3.core.Single;

/**
 * DAO (Data Access Object) interface for managing discount-related database operations
 * using Room Persistence Library. It handles CRUD operations related to the `Discount` entity.
 *
 * The methods use RxJava3 types (`Single`, `Maybe`) for asynchronous operations.
 */
@Dao
public interface IDiscountDao {

    /**
     * Inserts a new discount record into the database.
     *
     * @param discount The `Discount` object containing discount details.
     * @return A `Single` that emits the row ID of the inserted discount on success, or an error on failure.
     */
    @Insert
    Single<Long> createDiscount(Discount discount);

    /**
     * Retrieves a discount from the database based on the discount ID.
     *
     * @param discountId The ID of the discount to retrieve.
     * @return A `Maybe` that emits the `Discount` if found, completes if no discount is found, or emits an error.
     */
    @Query("SELECT * FROM discounts WHERE discount_id = :discountId")
    Maybe<Discount> getDiscount(int discountId);

    /**
     * Checks if a discount with a specific type and value already exists in the database.
     *
     * @param discountType The type of the discount (e.g., percentage, fixed).
     * @param discountValue The value of the discount (e.g., 10% or $20).
     * @return A `Maybe` that emits the `Discount` if it exists, completes if no such discount is found, or emits an error.
     */
    @Query("SELECT * FROM discounts WHERE discount_type = :discountType AND discount_value = :discountValue")
    Maybe<Discount> isExistingDiscount(String discountType, double discountValue);
}
