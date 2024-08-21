package com.example.ecommerce.model;

public class Product {
    private final int _productId;
    private String productName;
    private String productDescription;
    private Double productPrice;
    private String productImage;
    private Double productDiscount;
    private Integer productQuantity;
    private String productBrand;
    private String productCategory;

    public Product(int productId, String productName, String productDescription, Double productPrice, String productImage, Double productDiscount, Integer productQuantity, String productBrand, String productCategory) {
        this._productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.productPrice = productPrice;
        this.productImage = productImage;
        this.productDiscount = productDiscount;
        this.productQuantity = productQuantity;
        this.productBrand = productBrand;
        this.productCategory = productCategory;
    }

    public int getProductId() {
        return _productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public Double getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Double productPrice) {
        this.productPrice = productPrice;
    }

    public String getProductImage() {
        return productImage;
    }

    public void setProductImage(String productImage) {
        this.productImage = productImage;
    }

    public Double getProductDiscount() {
        return productDiscount;
    }

    public void setProductDiscount(Double productDiscount) {
        this.productDiscount = productDiscount;
    }

    public Integer getProductQuantity() {
        return productQuantity;
    }

    public void setProductQuantity(Integer productQuantity) {
        this.productQuantity = productQuantity;
    }

    public String getProductBrand() {
        return productBrand;
    }

    public void setProductBrand(String productBrand) {
        this.productBrand = productBrand;
    }

    public String getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(String productCategory) {
        this.productCategory = productCategory;
    }
}
