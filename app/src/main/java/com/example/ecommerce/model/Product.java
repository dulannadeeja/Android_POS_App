package com.example.ecommerce.model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "products")
public class Product implements Parcelable {

    // Empty constructor for Room
    public Product() {
    }

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "product_id")
    private  int _productId;

    @ColumnInfo(name = "product_name")
    private  String productName;

    @ColumnInfo(name = "product_description")
    private  String productDescription;

    @ColumnInfo(name = "product_price", typeAffinity = ColumnInfo.REAL)
    private  Double productPrice;

    @ColumnInfo(name = "product_cost", typeAffinity = ColumnInfo.REAL)
    private  Double productCost;

    @ColumnInfo(name = "product_image")
    private  String productImage;

    @ColumnInfo(name = "product_discount", typeAffinity = ColumnInfo.REAL)
    private  Double productDiscount;

    @ColumnInfo(name = "product_quantity", typeAffinity = ColumnInfo.INTEGER)
    private  Integer productQuantity;

    @ColumnInfo(name = "product_brand")
    private  String productBrand;

    @ColumnInfo(name = "product_category")
    private  String productCategory;


    public Product(ProductBuilder builder) {
        this._productId = builder._productId;
        this.productName = builder.productName;
        this.productDescription = builder.productDescription;
        this.productPrice = builder.productPrice;
        this.productCost = builder.productCost;
        this.productImage = builder.productImage;
        this.productDiscount = builder.productDiscount;
        this.productQuantity = builder.productQuantity;
        this.productBrand = builder.productBrand;
        this.productCategory = builder.productCategory;
    }

    protected Product(Parcel in) {
        _productId = in.readInt();
        productName = in.readString();
        productDescription = in.readString();
        productPrice = in.readDouble();
        productCost = in.readDouble();
        productImage = in.readString();
        productDiscount = in.readDouble();
        productQuantity = in.readInt();
        productBrand = in.readString();
        productCategory = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(_productId);
        dest.writeString(productName);
        dest.writeString(productDescription);
        dest.writeDouble(productPrice);
        dest.writeDouble(productCost);
        dest.writeString(productImage);
        dest.writeDouble(productDiscount);
        dest.writeInt(productQuantity);
        dest.writeString(productBrand);
        dest.writeString(productCategory);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static class ProductBuilder{
        private int _productId;
        private String productName;
        private String productDescription;
        private Double productCost;
        private Double productPrice;
        private String productImage;
        private Double productDiscount;
        private Integer productQuantity;
        private String productBrand;
        private String productCategory;

        public ProductBuilder withProductId(int _productId) {
            this._productId = _productId;
            return this;
        }

        public ProductBuilder withProductInfo(String productName, String productDescription, String productBrand, String productCategory) {
            this.productName = productName;
            this.productDescription = productDescription;
            this.productBrand = productBrand;
            this.productCategory = productCategory;
            return this;
        }

        public ProductBuilder withPricing(Double productCost, Double productPrice) {
            this.productCost = productCost;
            this.productPrice = productPrice;
            return this;
        }

        public ProductBuilder withImage(String productImage) {
            this.productImage = productImage;
            return this;
        }

        public ProductBuilder withDiscount(Double productDiscount) {
            this.productDiscount = productDiscount;
            return this;
        }

        public ProductBuilder withQuantity(Integer productQuantity) {
            this.productQuantity = productQuantity;
            return this;
        }

        public Product buildProduct() {
            return new Product(this);
        }
    }

    public int get_productId() {
        return _productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public Double getProductCost() {
        return productCost;
    }

    public String getProductImage() {
        return productImage;
    }

    public Double getProductDiscount() {
        return productDiscount;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void set_productId(int _productId) {
        this._productId = _productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public void setProductCost(Double productCost) {
        this.productCost = productCost;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public void setProductDiscount(Double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
}


