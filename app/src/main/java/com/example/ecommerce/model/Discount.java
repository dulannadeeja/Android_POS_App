package com.example.ecommerce.model;

public class Discount {
    private int discountId;
    private final String discountType;
    private final double discountValue;

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
}
