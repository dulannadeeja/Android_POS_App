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
    public double getDiscountAmount(double totalAmount) throws Exception {
        try {
            Discount currentDiscount = getCurrentDiscount();
            if(currentDiscount == null) {
                return 0.0;
            }
            return totalAmount * currentDiscount.getDiscountValue();
        } catch (Exception e) {
            throw new RuntimeException("Error getting discount amount", e);
        }
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
