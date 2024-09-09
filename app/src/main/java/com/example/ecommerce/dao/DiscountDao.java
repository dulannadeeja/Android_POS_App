package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.ecommerce.model.Discount;
import com.example.ecommerce.utils.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import kotlin.Suppress;

public class DiscountDao implements IDiscountDao {
    private final DatabaseHelper databaseHelper;

    public DiscountDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public int createDiscount(Discount discount) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            String query = "INSERT INTO " + DatabaseHelper.TABLE_DISCOUNTS + " (" +
                    DatabaseHelper.COLUMN_DISCOUNT_TYPE + ", " +
                    DatabaseHelper.COLUMN_DISCOUNT_VALUE + ") " +
                    "VALUES (?, ?, ?)";

            ContentValues args = new ContentValues();
            args.put(DatabaseHelper.COLUMN_DISCOUNT_TYPE, discount.getDiscountType());
            args.put(DatabaseHelper.COLUMN_DISCOUNT_VALUE, discount.getDiscountValue());

            int discountId = (int) db.insertOrThrow(DatabaseHelper.TABLE_DISCOUNTS, null, args);
            return discountId;
        } catch (Exception e) {
            throw new RuntimeException("Error creating discount", e);
        }
    }

    @SuppressLint("Range")
    @Override
    public Discount getDiscount(int discountId) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_DISCOUNTS + " WHERE " + DatabaseHelper.COLUMN_DISCOUNT_ID + " = " + discountId;
            Cursor cursor = db.rawQuery(query, null);
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();

                String discountType = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_TYPE));
                double discountValue = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_VALUE));
                Discount discount = new Discount.DiscountBuilder()
                        .withDiscountId(discountId)
                        .withDiscountType(discountType)
                        .withDiscountValue(discountValue)
                        .build();

                cursor.close();
                return discount;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting discount", e);
        }
    }

    @Override
    @SuppressLint("Range")
    public Discount isExistingDiscount(String discountType, double discountValue) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_DISCOUNTS + " WHERE " + DatabaseHelper.COLUMN_DISCOUNT_TYPE + " = ? AND " + DatabaseHelper.COLUMN_DISCOUNT_VALUE + " = ?";
            Cursor cursor = db.rawQuery(query, new String[]{discountType, String.valueOf(discountValue)});
            if (cursor.getCount() == 0) {
                return null;
            } else {
                cursor.moveToFirst();

                 int discountId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_ID));
                Discount discount = new Discount.DiscountBuilder()
                        .withDiscountId(discountId)
                        .withDiscountType(discountType)
                        .withDiscountValue(discountValue)
                        .build();

                cursor.close();
                return discount;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting discount", e);
        }
    }
}
