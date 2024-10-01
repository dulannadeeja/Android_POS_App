package com.example.ecommerce.dao;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.example.ecommerce.model.Payment;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.ArrayList;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Maybe;

public class PaymentDao implements IPaymentDao {
    private DatabaseHelper databaseHelper;

    public PaymentDao(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    @Override
    public Completable createPayment(Payment payment) {
        return Completable.create(emitter -> {
            SQLiteDatabase db = null;
            try {
                db = databaseHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(DatabaseHelper.COLUMN_PAYMENT_ORDER_ID, payment.getOrderId());
                values.put(DatabaseHelper.COLUMN_PAYMENT_AMOUNT, payment.getPaymentAmount());
                values.put(DatabaseHelper.COLUMN_PAYMENT_METHOD, payment.getPaymentMethod());
                values.put(DatabaseHelper.COLUMN_PAYMENT_DATE, payment.getPaymentDate());

                db.insertOrThrow(DatabaseHelper.TABLE_PAYMENTS, null, values);

                emitter.onComplete();
            } catch (SQLiteException e) {
                emitter.onError(e);    // Emit an error in case of an SQLite exception
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();        // Close the database connection
                }
            }
        });
    }

    @SuppressLint("Range")
    @Override
    public Maybe<ArrayList<Payment>> filterPaymentsByOrder(int orderId) {
        return Maybe.create(emitter -> {
            SQLiteDatabase db = null;
            try {
                db = databaseHelper.getReadableDatabase();
                ArrayList<Payment> payments = new ArrayList<>();
                Cursor cursor = db.query(DatabaseHelper.TABLE_PAYMENTS, null, DatabaseHelper.COLUMN_PAYMENT_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)}, null, null, null);
                if(cursor == null) {
                    emitter.onComplete();
                    return;
                }
                while (cursor.moveToNext()) {
                    Payment payment = new Payment(
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_METHOD)),
                            cursor.getDouble(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_AMOUNT)),
                            cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_DATE)),
                            cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_ORDER_ID))
                    );
                    payment.setPaymentId(cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_PAYMENT_ID)));
                    payments.add(payment);
                }
                cursor.close();
                emitter.onSuccess(payments);
            } catch (SQLiteException e) {
                emitter.onError(e);    // Emit an error in case of an SQLite exception
            } finally {
                if (db != null && db.isOpen()) {
                    db.close();        // Close the database connection
                }
            }
        });
    }
}
