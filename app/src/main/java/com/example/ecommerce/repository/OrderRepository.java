package com.example.ecommerce.repository;

import com.example.ecommerce.dao.IOrderDao;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.OrderItem;

import java.util.ArrayList;

public class OrderRepository implements IOrderRepository {
    private static final String TAG = "OrderRepository";
    private final IOrderDao orderDao;

    public OrderRepository(IOrderDao orderDao) {
        this.orderDao = orderDao;
    }

    @Override
    public long handleNewOrder(ArrayList<CartItem> cartItems,double orderTotal, double taxAndCharges, double subTotal, int discountId, double discountAmount, int customerId) {

        String date = (new java.util.Date()).toString();

        Order.OrderBuilder newOrderBuilder = new Order.OrderBuilder(date, orderTotal, "PENDING", taxAndCharges, subTotal)
                .withPayment(0, subTotal)
                .withCustomerId(customerId);

        if (discountId != 0 && discountAmount != 0) {
            newOrderBuilder.withDiscount(discountId, discountAmount);
        }

        Order newOrder = newOrderBuilder.build();

        long orderId = orderDao.createOrder(newOrder);

        // create order items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem((int) orderId, cartItem.getProductId(), cartItem.getQuantity());
            orderDao.createOrderItem(orderItem);
        }

        return orderId;
    }

    @Override
    public void makeOrderPayment(double paymentAmount, int orderId) {
        Order order = orderDao.getOrderById(orderId);
        double paidAmount = order.getPaidAmount() + paymentAmount;
        double dueAmount = order.getDueAmount() - paymentAmount;
        String orderStatus = dueAmount == 0 ? "PAID" : "PENDING";
        orderDao.updateOrderPayment(orderId, paidAmount, dueAmount, orderStatus);
    }

    @Override
    public ArrayList<Order> getPendingOrders() {
        ArrayList<Order> pendingOrders = orderDao.filterOrdersByStatus("PENDING");
        // reordering the list to show the latest order first
        ArrayList<Order> reversedList = new ArrayList<>();
        for (int i = pendingOrders.size() - 1; i >= 0; i--) {
            reversedList.add(pendingOrders.get(i));
        }
        return reversedList;
    }

    @Override
    public ArrayList<OrderItem> getOrderItems(int orderId) {
        return orderDao.getOrderItems(orderId);
    }
}
