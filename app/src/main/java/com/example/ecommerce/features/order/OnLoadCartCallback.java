package com.example.ecommerce.features.order;

import com.example.ecommerce.model.Cart;

public interface OnLoadCartCallback {
    void onLoadCart(Cart cartToLoad);
}
