package com.example.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Model class representing a Customer entity in the `customers` table.
 * Implements Parcelable to allow easy data passing between Android components.
 */
@Entity(tableName = "customers")
public class Customer implements Parcelable {

    // Empty constructor required by Room for data operations
    public Customer() {}

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "customer_id")
    private int customerId;

    @ColumnInfo(name = "customer_first_name")
    private String firstName;

    @ColumnInfo(name = "customer_last_name")
    private String lastName;

    @ColumnInfo(name = "customer_email")
    private String email;

    @ColumnInfo(name = "customer_phone")
    private String phone;

    @ColumnInfo(name = "customer_address")
    private String address;

    @ColumnInfo(name = "customer_city")
    private String city;

    @ColumnInfo(name = "customer_region")
    private String region;

    @ColumnInfo(name = "customer_gender")
    private String gender;

    @ColumnInfo(name = "customer_photo")
    private String photo;

    /**
     * Builder-based constructor for creating Customer objects.
     * This ensures flexibility in object creation.
     *
     * @param builder CustomerBuilder object containing the necessary fields.
     */
    public Customer(CustomerBuilder builder) {
        this.customerId = builder.customerId;
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.email = builder.email;
        this.phone = builder.phone;
        this.address = builder.address;
        this.city = builder.city;
        this.region = builder.region;
        this.gender = builder.gender;
        this.photo = builder.photo;
    }

    /**
     * Parcel-based constructor for implementing Parcelable.
     * Used when passing data between Android components.
     */
    protected Customer(Parcel in) {
        customerId = in.readInt();
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        phone = in.readString();
        address = in.readString();
        city = in.readString();
        region = in.readString();
        gender = in.readString();
        photo = in.readString();
    }

    @Override
    public void writeToParcel(@NonNull Parcel parcel, int i) {
        parcel.writeInt(customerId);
        parcel.writeString(firstName);
        parcel.writeString(lastName);
        parcel.writeString(email);
        parcel.writeString(phone);
        parcel.writeString(address);
        parcel.writeString(city);
        parcel.writeString(region);
        parcel.writeString(gender);
        parcel.writeString(photo);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Customer> CREATOR = new Creator<Customer>() {
        @Override
        public Customer createFromParcel(Parcel in) {
            return new Customer(in);
        }

        @Override
        public Customer[] newArray(int size) {
            return new Customer[size];
        }
    };

    // Getters and setters for each field

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    /**
     * Builder class for constructing Customer objects.
     * This pattern allows for flexible and maintainable object creation.
     */
    public static class CustomerBuilder {
        private int customerId;
        private String firstName;
        private String lastName;
        private String email;
        private String phone;
        private String address;
        private String city;
        private String region;
        private String gender;
        private String photo;

        public CustomerBuilder withCustomerId(int customerId) {
            this.customerId = customerId;
            return this;
        }

        public CustomerBuilder withName(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
            return this;
        }

        public CustomerBuilder withEmail(String email) {
            this.email = email;
            return this;
        }

        public CustomerBuilder withPhone(String phone) {
            this.phone = phone;
            return this;
        }

        public CustomerBuilder withAddress(String address, String city, String region) {
            this.address = address;
            this.city = city;
            this.region = region;
            return this;
        }

        public CustomerBuilder withGender(String gender) {
            this.gender = gender;
            return this;
        }

        public CustomerBuilder withPhoto(String photo) {
            this.photo = photo;
            return this;
        }

        public Customer buildCustomer() {
            return new Customer(this);
        }
    }
}
