package com.example.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Customer implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

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

    public int getCustomerId() {
        return customerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getAddress() {
        return address;
    }

    public String getGender() {
        return gender;
    }

    public String getCity() { return city; }

    public String getRegion() { return region; }

    public String getPhoto() { return photo; }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setCity(String city) { this.city = city; }

    public void setRegion(String region) { this.region = region; }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public void setPhoto(String photo) { this.photo = photo; }
}
