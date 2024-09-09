package com.example.ecommerce.ui.customers.create_customer;

import android.net.Uri;

public interface OnPhotoChoosedCallback {
    void onSuccessfulPhotoChoose(Uri uri);
    void onFailedPhotoChoose(String message);
    void onPhotoRemoved();
}
