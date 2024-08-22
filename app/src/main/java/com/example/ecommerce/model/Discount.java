package com.example.ecommerce.model;

import java.util.Date;

public class Discount {
    private int discountId;
    private final String discountType;
    private final double discountValue;
    private final Date startDate;
    private Date endDate;
    private final Boolean isActive;

    public Discount(String discountType, double discountValue, Date startDate,  Boolean isActive) {
        this.discountType = discountType;
        this.discountValue = discountValue;
        this.startDate = startDate;
        this.isActive = isActive;
    }

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public int getDiscountId() {
        return discountId;
    }

    public String getDiscountType() {
        return discountType;
    }

    public double getDiscountValue() {
        return discountValue;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public Boolean getActive() {
        return isActive;
    }
}
