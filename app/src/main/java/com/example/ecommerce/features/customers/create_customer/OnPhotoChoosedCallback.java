package com.example.ecommerce.features.customers.create_customer;

import android.net.Uri;

public interface OnPhotoChoosedCallback {
    void onSuccessfulPhotoChoose(Uri uri);
    void onFailedPhotoChoose(String message);
    void onPhotoRemoved();
}
