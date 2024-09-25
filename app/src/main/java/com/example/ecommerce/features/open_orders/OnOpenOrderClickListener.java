package com.example.ecommerce.features.open_orders;

import com.example.ecommerce.model.Order;

public interface OnOpenOrderClickListener {
    void onEditOrderClick(Order order);
    void onViewOrderClick(Order order);
    void onPrintReceiptClick(Order order);
}
