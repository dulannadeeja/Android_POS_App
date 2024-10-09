package com.example.ecommerce.model;

import androidx.room.ColumnInfo;

public class CartProduct {

    @ColumnInfo(name = "cart_product_id")
    private int cartProductId;

    @ColumnInfo(name = "cart_product_quantity")
    private int cartProductQuantity;

    @ColumnInfo(name = "product_stock")
    private int productStock;

    // Constructor
    public CartProduct(int cartProductId, int cartProductQuantity, int productStock) {
        this.cartProductId = cartProductId;
        this.cartProductQuantity = cartProductQuantity;
        this.productStock = productStock;
    }

    // Getters
    public int getCartProductId() {
        return cartProductId;
    }

    public int getCartProductQuantity() {
        return cartProductQuantity;
    }

    public int getProductStock() {
        return productStock;
    }
}
