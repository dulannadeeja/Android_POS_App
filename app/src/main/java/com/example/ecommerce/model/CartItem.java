package com.example.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

public class CartItem implements Parcelable {
    private final Integer _cartItemId; // Optional, can be null
    private final int productId;
    private int quantity;
    private double price;
    private String productName;
    private double discount;

    // Private constructor to prevent direct instantiation
    private CartItem(Builder builder) {
        this._cartItemId = builder._cartItemId;
        this.productId = builder.productId;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.productName = builder.productName;
        this.discount = builder.discount;
    }

    // Parcelable constructor
    protected CartItem(Parcel in) {
        if (in.readByte() == 0) {
            _cartItemId = null;
        } else {
            _cartItemId = in.readInt();
        }
        productId = in.readInt();
        quantity = in.readInt();
        price = in.readDouble();
        productName = in.readString();
        discount = in.readDouble();
    }

    public static final Creator<CartItem> CREATOR = new Creator<CartItem>() {
        @Override
        public CartItem createFromParcel(Parcel in) {
            return new CartItem(in);
        }

        @Override
        public CartItem[] newArray(int size) {
            return new CartItem[size];
        }
    };

    // Getters
    public Integer getCartItemId() {
        return _cartItemId;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getPrice() {
        return price;
    }

    public String getProductName() {
        return productName;
    }

    public double getDiscount() {
        return discount;
    }

    // Setters for mutable fields
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    // Parcelable implementation
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (_cartItemId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(_cartItemId);
        }
        dest.writeInt(productId);
        dest.writeInt(quantity);
        dest.writeDouble(price);
        dest.writeString(productName);
        dest.writeDouble(discount);
    }

    // Static Builder class
    public static class Builder {
        private Integer _cartItemId = null;  // Optional field, default is null
        private final int productId;
        private int quantity;
        private double price;
        private String productName;
        private double discount;

        // Constructor for mandatory fields
        public Builder(int productId) {
            this.productId = productId;
        }

        // Setter method for optional cartItemId with new naming convention
        public Builder withCartItemId(int cartItemId) {
            this._cartItemId = cartItemId;
            return this;
        }

        // Setter methods for optional fields in the builder
        public Builder withQuantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder withPrice(double price) {
            this.price = price;
            return this;
        }

        public Builder withProductName(String productName) {
            this.productName = productName;
            return this;
        }

        public Builder withDiscount(double discount) {
            this.discount = discount;
            return this;
        }

        // Build method to create a CartItem instance
        public CartItem build() {
            return new CartItem(this);
        }
    }
}
