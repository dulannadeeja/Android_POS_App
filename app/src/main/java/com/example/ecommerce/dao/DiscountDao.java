package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
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
    public void createDiscount(String discountType, double discountValue, Date startDate) throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            String query = "INSERT INTO " + DatabaseHelper.TABLE_DISCOUNTS + " (" +
                    DatabaseHelper.COLUMN_DISCOUNT_TYPE + ", " +
                    DatabaseHelper.COLUMN_DISCOUNT_VALUE + ", " +
                    DatabaseHelper.COLUMN_START_DATE + ", " +
                    DatabaseHelper.COLUMN_IS_ACTIVE + ", " +
                    DatabaseHelper.COLUMN_END_DATE + ") " +
                    "VALUES (?, ?, ?, ?, ?)";

            // Prepare the date string in the desired format (e.g., "yyyy-MM-dd")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String startDateString = sdf.format(startDate);

            // Execute the insert statement with the properly formatted parameters
            db.execSQL(query, new Object[]{discountType, discountValue, startDateString, 1, startDateString});
            Log.d("DiscountDao", "Discount created: " + discountType + " " + discountValue + " " + startDateString);
        } catch (Exception e) {
            throw new RuntimeException("Error creating discount", e);
        }
    }

    @Override
    public void inactivateDiscounts() throws Exception {
        try (SQLiteDatabase db = databaseHelper.getWritableDatabase()) {
            String query = "UPDATE " + DatabaseHelper.TABLE_DISCOUNTS + " SET " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 0 WHERE " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";
            db.execSQL(query);
        } catch (Exception e) {
            throw new RuntimeException("Error inactivating discounts", e);
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
                Date startDate = new Date(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE)));
                Boolean isActive = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1;
                Date endDate = new Date(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE)));

                Discount discount = new Discount(discountType, discountValue, startDate, isActive);
                discount.setDiscountId(discountId);
                discount.setEndDate(endDate);

                cursor.close();
                return discount;
            }

        } catch (Exception e) {
            throw new RuntimeException("Error getting discount", e);
        }
    }


    @SuppressLint("Range")
    @Override
    public ArrayList<Discount> getActiveDiscounts() throws Exception {
        try (SQLiteDatabase db = databaseHelper.getReadableDatabase()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String query = "SELECT * FROM " + DatabaseHelper.TABLE_DISCOUNTS + " WHERE " + DatabaseHelper.COLUMN_IS_ACTIVE + " = 1";
            Cursor cursor = db.rawQuery(query, null);
            ArrayList<Discount> discounts = new ArrayList<>();
            while (cursor.moveToNext()) {
                int discountId = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_ID));
                String discountType = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_TYPE));
                double discountValue = cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_DISCOUNT_VALUE));
                Date startDate = formatter.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_START_DATE)));
                Boolean isActive = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_IS_ACTIVE)) == 1;
                Date endDate = formatter.parse(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_END_DATE)));

                Discount discount = new Discount(discountType, discountValue, startDate, isActive);
                discount.setDiscountId(discountId);
                discount.setEndDate(endDate);
                discounts.add(discount);
            }
            cursor.close();
            return discounts;
        } catch (Exception e) {
            throw new RuntimeException("Error getting discounts", e);
        }
    }
}
