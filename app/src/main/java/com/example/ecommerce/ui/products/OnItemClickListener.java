package com.example.ecommerce.ui.products;

import com.example.ecommerce.model.Product;

public interface OnItemClickListener {
    void onItemClick(int productId);
    void onItemLongClick(int productId);
    void onProductInfoClick(Product product);
    void onAddCustomerClick();
}
