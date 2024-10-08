package com.example.ecommerce.model;

public class Payment {
    private int _paymentId;
    private final String paymentMethod;
    private final double paymentAmount;
    private final String paymentDate;
    private final int orderId;
    private final Boolean isPaid;
    private final Double change;

    private Payment(Builder builder) {
        this._paymentId = builder._paymentId;
        this.paymentMethod = builder.paymentMethod;
        this.paymentAmount = builder.paymentAmount;
        this.paymentDate = builder.paymentDate;
        this.orderId = builder.orderId;
        this.isPaid = builder.isPaid;
        this.change = builder.change;
    }

    public static class Builder {
        private int _paymentId;
        private String paymentMethod;
        private double paymentAmount;
        private String paymentDate;
        private int orderId;
        private Boolean isPaid = false;
        private Double change = 0.0;

        public Builder withPaymentId(int paymentId) {
            this._paymentId = paymentId;
            return this;
        }

        public Builder withPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        public Builder withPaymentAmount(double paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        public Builder withPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        public Builder withOrderId(int orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder withIsPaid(Boolean isPaid) {
            this.isPaid = isPaid;
            return this;
        }

        public Builder withChange(Double change) {
            this.change = change;
            return this;
        }

        public Payment build() {
            return new Payment(this);
        }
    }

    // Getters
    public int getPaymentId() {
        return _paymentId;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public double getPaymentAmount() {
        return paymentAmount;
    }

    public int getOrderId() {
        return orderId;
    }

    public Boolean getIsPaid() {
        return isPaid;
    }

    public Double getChange() {
        return change;
    }
}
