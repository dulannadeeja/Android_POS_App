package com.example.ecommerce.features.products;

import java.util.ArrayList;
import java.util.HashMap;

public interface OnCartChangesAppliedCallback {
    void onSuccessfulCartChanges(HashMap<Integer, Integer> updatedCartQuantityMap, HashMap<Integer, Integer> productStockMap, ArrayList<Integer> productIdsToUpdate);
    void onFailedCartChanges(String errorMessage);
}
