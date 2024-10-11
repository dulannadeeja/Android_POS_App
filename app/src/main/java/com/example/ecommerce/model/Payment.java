package com.example.ecommerce.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverter;
import androidx.room.TypeConverters;

/**
 * Represents a payment made by a customer for an order.
 * This entity stores details of the payment, including method, amount, and whether it has been fully paid.
 * Each payment is linked to a specific order.
 */
@Entity(tableName = "payments")
public class Payment {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "payment_id")
    private final int paymentId;

    @ColumnInfo(name = "payment_method")
    private final String paymentMethod;

    @ColumnInfo(name = "payment_amount", typeAffinity = ColumnInfo.REAL, defaultValue = "0.0")
    private final double paymentAmount;

    @ColumnInfo(name = "payment_date")
    private final String paymentDate;

    @ColumnInfo(name = "order_id", typeAffinity = ColumnInfo.INTEGER, defaultValue = "0")
    private final int orderId;

    @TypeConverters(BooleanConverter.class) // Add TypeConverter to map between Boolean and Integer
    private final boolean isPaid;

    @ColumnInfo(name = "change_amount", typeAffinity = ColumnInfo.REAL, defaultValue = "0.0")
    private final double changeAmount;

    public Payment(int paymentId, String paymentMethod, double paymentAmount, String paymentDate, int orderId, boolean isPaid, double changeAmount) {
        this.paymentId = paymentId;
        this.paymentMethod = paymentMethod;
        this.paymentAmount = paymentAmount;
        this.paymentDate = paymentDate;
        this.orderId = orderId;
        this.isPaid = isPaid;
        this.changeAmount = changeAmount;
    }

    /**
     * Private constructor that initializes the Payment object using the Builder.
     *
     * @param builder the builder object containing all the required fields for Payment.
     */
    private Payment(Builder builder) {
        this.paymentId = builder.paymentId;
        this.paymentMethod = builder.paymentMethod;
        this.paymentAmount = builder.paymentAmount;
        this.paymentDate = builder.paymentDate;
        this.orderId = builder.orderId;
        this.isPaid = builder.isPaid;
        this.changeAmount = builder.changeAmount;
    }

    /**
     * Builder class for constructing immutable Payment instances.
     * Use this class to set up all the necessary attributes for a Payment object in a fluid manner.
     */
    public static class Builder {
        private int paymentId;
        private String paymentMethod;
        private double paymentAmount;
        private String paymentDate;
        private int orderId;
        private boolean isPaid = false;
        private double changeAmount = 0.0;

        /**
         * Sets the payment ID for this payment.
         *
         * @param paymentId the unique ID for the payment.
         * @return the builder instance.
         */
        public Builder withPaymentId(int paymentId) {
            this.paymentId = paymentId;
            return this;
        }

        /**
         * Sets the payment method (e.g., cash, credit card) for this payment.
         *
         * @param paymentMethod the method used for payment.
         * @return the builder instance.
         */
        public Builder withPaymentMethod(String paymentMethod) {
            this.paymentMethod = paymentMethod;
            return this;
        }

        /**
         * Sets the amount of the payment.
         *
         * @param paymentAmount the amount paid.
         * @return the builder instance.
         */
        public Builder withPaymentAmount(double paymentAmount) {
            this.paymentAmount = paymentAmount;
            return this;
        }

        /**
         * Sets the date of the payment.
         *
         * @param paymentDate the date when the payment was made.
         * @return the builder instance.
         */
        public Builder withPaymentDate(String paymentDate) {
            this.paymentDate = paymentDate;
            return this;
        }

        /**
         * Sets the order ID for which this payment was made.
         *
         * @param orderId the ID of the associated order.
         * @return the builder instance.
         */
        public Builder withOrderId(int orderId) {
            this.orderId = orderId;
            return this;
        }

        /**
         * Specifies whether the payment has been fully processed.
         *
         * @param isPaid true if the payment is complete, false otherwise.
         * @return the builder instance.
         */
        public Builder withIsPaid(boolean isPaid) {
            this.isPaid = isPaid;
            return this;
        }

        /**
         * Sets the amount of change to be returned after the payment.
         *
         * @param changeAmount the amount of change returned to the customer.
         * @return the builder instance.
         */
        public Builder withChangeAmount(double changeAmount) {
            this.changeAmount = changeAmount;
            return this;
        }

        /**
         * Builds and returns a Payment object with the specified attributes.
         *
         * @return a new instance of Payment.
         */
        public Payment build() {
            return new Payment(this);
        }
    }

    // Getters with professional descriptions

    /**
     * Gets the unique ID of the payment.
     *
     * @return the payment ID.
     */
    public int getPaymentId() {
        return paymentId;
    }

    /**
     * Gets the method used for this payment (e.g., cash, card).
     *
     * @return the payment method.
     */
    public String getPaymentMethod() {
        return paymentMethod;
    }

    /**
     * Gets the total amount paid.
     *
     * @return the payment amount.
     */
    public double getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Gets the date when the payment was made.
     *
     * @return the payment date.
     */
    public String getPaymentDate() {
        return paymentDate;
    }

    /**
     * Gets the associated order ID for this payment.
     *
     * @return the order ID.
     */
    public int getOrderId() {
        return orderId;
    }

    /**
     * Indicates whether the payment has been fully processed.
     *
     * @return true if the payment is fully processed, false otherwise.
     */
    public boolean isPaid() {
        return isPaid;
    }

    /**
     * Gets the change amount returned to the customer, if applicable.
     *
     * @return the amount of change.
     */
    public double getChangeAmount() {
        return changeAmount;
    }
}

