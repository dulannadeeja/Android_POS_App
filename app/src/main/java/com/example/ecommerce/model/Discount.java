package com.example.ecommerce.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "discounts")
public class Discount {

    // Empty constructor for Room
    public Discount() {
    }

    @ColumnInfo(name = "discount_id")
    @PrimaryKey(autoGenerate = true)
    private int discountId;

    @ColumnInfo(name = "discount_type")
    private String discountType;

    @ColumnInfo(name = "discount_value")
    private double discountValue;

    private Discount(DiscountBuilder builder) {
        this.discountId = builder.discountId;
        this.discountType = builder.discountType;
        this.discountValue = builder.discountValue;
    }

    public static class DiscountBuilder {
        private int discountId;
        private String discountType;
        private double discountValue;

        public DiscountBuilder withDiscountId(int discountId) {
            this.discountId = discountId;
            return this;
        }

        public DiscountBuilder withDiscountType(String discountType) {
            this.discountType = discountType;
            return this;
        }

        public DiscountBuilder withDiscountValue(double discountValue) {
            this.discountValue = discountValue;
            return this;
        }

        public Discount build() {
            return new Discount(this);
        }
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

    public void setDiscountId(int discountId) {
        this.discountId = discountId;
    }

    public void setDiscountType(String discountType) {
        this.discountType = discountType;
    }

    public void setDiscountValue(double discountValue) {
        this.discountValue = discountValue;
    }
}
