package com.example.ecommerce.utils;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.R;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.features.customers.OnCustomerClickListener;

import java.util.ArrayList;

public class CustomersAdapter extends RecyclerView.Adapter<CustomersAdapter.ViewHolder> {
    private final ArrayList<Customer> customers;
    private final OnCustomerClickListener onCustomerClickListener;

    public CustomersAdapter(ArrayList<Customer> customers, OnCustomerClickListener onCustomerClickListener) {
        this.customers = customers;
        this.onCustomerClickListener = onCustomerClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_customers, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Customer customer = customers.get(position);
        holder.bind(customer, onCustomerClickListener, holder);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCustomerName;
        private final TextView tvCustomerID;
        private final ImageView ivCustomerAvatar, useCustomerButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCustomerName = itemView.findViewById(R.id.tv_customer_name);
            tvCustomerID = itemView.findViewById(R.id.tv_customer_id);
            ivCustomerAvatar = itemView.findViewById(R.id.iv_customer_avatar);
            useCustomerButton = itemView.findViewById(R.id.use_customer_button);
        }

        public void bind(Customer customer, OnCustomerClickListener listener, ViewHolder holder) {
            tvCustomerName.setText(String.format("%s %s", customer.getFirstName(), customer.getLastName()));
            tvCustomerID.setText(String.format("Customer ID: %s", customer.getCustomerId()));
            if(customer.getPhoto() != null){
                ivCustomerAvatar.setImageURI(Uri.parse(customer.getPhoto()));
            }
            useCustomerButton.setOnClickListener(v -> listener.onAddCustomerClicked(customer));
            holder.itemView.setOnClickListener(v -> listener.onCustomerClick(customer));
        }
    }

    public void setCustomers(ArrayList<Customer> customers) {
        this.customers.clear();
        this.customers.addAll(customers);
        notifyDataSetChanged();
    }

}
