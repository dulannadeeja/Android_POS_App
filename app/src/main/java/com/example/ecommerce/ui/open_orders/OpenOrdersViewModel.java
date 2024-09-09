package com.example.ecommerce.ui.open_orders;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.ecommerce.model.OpenOrderItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.repository.IOrderRepository;
import com.example.ecommerce.utils.DateHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class OpenOrdersViewModel extends ViewModel {
    private static final String TAG = "OpenOrdersViewModel";
    private final IOrderRepository orderRepository;
    private final MutableLiveData<ArrayList<OpenOrderItem>> ordersListLiveData = new MutableLiveData<>();

    public OpenOrdersViewModel(IOrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void getPendingOrders() {
        try {
            List<Order> pendingOrders = orderRepository.getPendingOrders();
            Map<String, List<Order>> ordersByDate = pendingOrders.stream()
                    .collect(Collectors.groupingBy(order -> DateHelper.formatDate(order.getOrderDate()), LinkedHashMap::new, Collectors.toList()));

            ArrayList<OpenOrderItem> ordersList = new ArrayList<>();
            ordersByDate.forEach((date, orders) -> {
                ordersList.add(new OpenOrderItem(date));
                orders.forEach(order -> ordersList.add(new OpenOrderItem(order)));
            });

            ordersListLiveData.postValue(ordersList);
        } catch (Exception e) {
            Log.e(TAG, "Error getting pending orders", e);
        }
    }

    public MutableLiveData<ArrayList<OpenOrderItem>> getOrdersListLiveData() {
        return ordersListLiveData;
    }

}
