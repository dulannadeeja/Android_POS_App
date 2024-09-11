package com.example.ecommerce.repository;

import android.content.SharedPreferences;
import android.util.Log;

import com.example.ecommerce.App;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.model.Discount;

import java.util.ArrayList;

public class DiscountRepository implements IDiscountRepository {
    private IDiscountDao discountDao;

    public DiscountRepository(IDiscountDao discountDao) {
        this.discountDao = discountDao;
    }

    @Override
    public int newDiscountHandler (Discount discount) throws Exception {
        try{
            int discountId = 0;
            Discount existingDiscount = discountDao.isExistingDiscount(discount.getDiscountType(), discount.getDiscountValue());
            if(existingDiscount == null){
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
        try{
            SharedPreferences discountPrefs = App.appModule.provideDiscountSharedPreferences();
            SharedPreferences.Editor editor = discountPrefs.edit();
            editor.putInt("discountId", discount.getDiscountId());
            editor.putString("discountType", discount.getDiscountType());
            editor.putFloat("discountValue", (float) discount.getDiscountValue());
            editor.apply();
        }catch (Exception e) {
            throw new RuntimeException("Error saving current discount", e);
        }
    }

    @Override
    public Discount getCurrentDiscount() throws Exception {
        try{
            SharedPreferences discountPrefs = App.appModule.provideDiscountSharedPreferences();
            if(!discountPrefs.contains("discountId") || !discountPrefs.contains("discountType") || !discountPrefs.contains("discountValue")){
                return null;
            }
            int discountId = discountPrefs.getInt("discountId", 0);
            String discountType = discountPrefs.getString("discountType", "");
            double discountValue = discountPrefs.getFloat("discountValue", 0);
            return new Discount.DiscountBuilder()
                    .withDiscountId(discountId)
                    .withDiscountType(discountType)
                    .withDiscountValue(discountValue)
                    .build();
        }catch (Exception e) {
            throw new RuntimeException("Error getting current discount", e);
        }
    }

    @Override
    public void clearCurrentDiscount() throws Exception {
        try{
            SharedPreferences discountPrefs = App.appModule.provideDiscountSharedPreferences();
            SharedPreferences.Editor editor = discountPrefs.edit();
            editor.clear();
            editor.apply();
        }catch (Exception e) {
            throw new RuntimeException("Error clearing current discount", e);
        }
    }

    @Override
    public double getDiscountAmount(double totalAmount, IApplyDiscountCallback callback) throws Exception {
        try {
            Discount currentDiscount = getCurrentDiscount();
            if(currentDiscount == null) {
                callback.onDiscountError("No active discount found");
                return totalAmount;
            }
            switch (currentDiscount.getDiscountType()){
                case "percentage":
                    callback.onDiscountApplied(totalAmount * currentDiscount.getDiscountValue() / 100, currentDiscount.getDiscountId());
                    break;
                case "fixed":
                    callback.onDiscountApplied(currentDiscount.getDiscountValue(), currentDiscount.getDiscountId());
                    break;
                default:
                    callback.onDiscountError("Invalid discount type");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error getting discount amount", e);
        }
        return totalAmount;
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
