package com.example.ecommerce.utils;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.model.OpenOrderItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.features.order.open_orders.OnOpenOrderClickListener;
import com.google.android.material.divider.MaterialDivider;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

public class OpenOrdersAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private static final int VIEW_TYPE_DATE_HEADER = 0;
    private static final int VIEW_TYPE_ORDER_ITEM = 1;

    private List<OpenOrderItem> openOrderItems = new ArrayList<>();
    private final OnOpenOrderClickListener listener;

    public OpenOrdersAdapter(List<OpenOrderItem> openOrderItems, OnOpenOrderClickListener listener) {
        this.openOrderItems = openOrderItems;
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        if (openOrderItems.get(position).isDate()) {
            return VIEW_TYPE_DATE_HEADER;
        } else {
            return VIEW_TYPE_ORDER_ITEM;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == VIEW_TYPE_DATE_HEADER){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_open_order_date_item, parent, false);
            return new DateHeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_open_order_item, parent, false);
            return new OrderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(getItemViewType(position) == VIEW_TYPE_DATE_HEADER){
            DateHeaderViewHolder dateHeaderViewHolder = (DateHeaderViewHolder) holder;
            dateHeaderViewHolder.dateTextView.setText(openOrderItems.get(position).getOrderDate());
            if(position == 0) {
                dateHeaderViewHolder.topDivider.setVisibility(View.GONE);
            }
        } else {
            OrderViewHolder orderViewHolder = (OrderViewHolder) holder;
            orderViewHolder.orderIdTextView.setText(String.format("Order - %s", openOrderItems.get(position).getOrder().get_orderId()));
            ZonedDateTime zonedDateTime = DateHelper.convertStringToZonedDateTime(openOrderItems.get(position).getOrder().getOrderDate());
            String timeAgo = DateHelper.timeAgo(zonedDateTime);
            orderViewHolder.orderTimeAgoTextView.setText(timeAgo);
            orderViewHolder.bind(openOrderItems.get(position).getOrder(), listener);
        }
    }

    @Override
    public int getItemCount() {
        return openOrderItems.size();
    }

    static class DateHeaderViewHolder extends RecyclerView.ViewHolder {
        TextView dateTextView;
        MaterialDivider topDivider;

        DateHeaderViewHolder(View itemView) {
            super(itemView);
            dateTextView = itemView.findViewById(R.id.tv_date_header);
            topDivider = itemView.findViewById(R.id.top_divider);
        }
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderIdTextView;
        TextView orderTimeAgoTextView;
        CheckBox orderCheckbox;
        ImageView viewOrderDetails, editOrder, printReceipt;

        OrderViewHolder(View itemView) {
            super(itemView);
            orderIdTextView = itemView.findViewById(R.id.tv_order_id);
            orderTimeAgoTextView = itemView.findViewById(R.id.tv_time_ago);
            orderCheckbox = itemView.findViewById(R.id.order_checkbox);
            viewOrderDetails = itemView.findViewById(R.id.view_order_button);
            editOrder = itemView.findViewById(R.id.edit_order_button);
            printReceipt = itemView.findViewById(R.id.print_order_button);
        }

        public void bind(final Order order, final OnOpenOrderClickListener listener){
            editOrder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onEditOrderClick(order);
                }
            });
            viewOrderDetails.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onViewOrderClick(order);
                }
            });
            printReceipt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onPrintReceiptClick(order);
                }
            });
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setOpenOrderItems(ArrayList<OpenOrderItem> openOrderItems) {
        this.openOrderItems.clear();
        if (openOrderItems != null && !openOrderItems.isEmpty()) {
            this.openOrderItems.addAll(openOrderItems);
        }
        notifyDataSetChanged();
    }
}
