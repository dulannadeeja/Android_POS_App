package com.example.ecommerce.repository;

import android.util.Log;

import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.model.Discount;

import java.util.ArrayList;

public class DiscountRepository implements IDiscountRepository {
    private IDiscountDao discountDao;

    public DiscountRepository(IDiscountDao discountDao) {
        this.discountDao = discountDao;
    }

    @Override
    public void addNewDiscount(Discount discount) throws Exception {
        try{
            discountDao.inactivateDiscounts();
            discountDao.createDiscount(discount.getDiscountType(), discount.getDiscountValue(), discount.getStartDate());
            Log.d("DiscountRepository", "New discount added: " + discount.toString());
        } catch (Exception e) {
            throw new RuntimeException("Error adding new discount", e);
        }
    }

    @Override
    public Discount getCurrentDiscount() throws Exception {
        try{
            ArrayList<Discount> discounts = discountDao.getActiveDiscounts();
            if(discounts.isEmpty()) {
                return null;
            }
            Discount currentDiscount = null;
            for(Discount discount : discounts) {
                if(currentDiscount == null || discount.getStartDate().after(currentDiscount.getStartDate())) {
                    currentDiscount = discount;
                }
            }
            return currentDiscount;
        }catch (Exception e) {
            throw new RuntimeException("Error getting current discount", e);
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
    public void clearDiscount() throws Exception {
        try {
            discountDao.inactivateDiscounts();
        } catch (Exception e) {
            throw new RuntimeException("Error clearing discount", e);
        }
    }
}
