package com.example.ecommerce.ui.open_orders;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentOpenOrdersBinding;
import com.example.ecommerce.model.OpenOrderItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.ui.cart.CartViewModel;
import com.example.ecommerce.utils.OpenOrdersAdapter;
import com.example.ecommerce.utils.ProductsAdapter;

import java.util.ArrayList;

public class OpenOrdersFragment extends DialogFragment implements OnOpenOrderClickListener {

    public static final String TAG = "OpenOrdersFragment";
    private FragmentOpenOrdersBinding binding;
    private CartViewModel cartViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentOpenOrdersBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the dialog to full-screen width and height
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        OpenOrdersViewModel openOrdersViewModel = new ViewModelProvider(this,new OpenOrdersViewModelFactory(App.appModule.provideOrderRepository())).get(OpenOrdersViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);
        binding.closeButton.setOnClickListener(v -> dismiss());

        // Initialize RecyclerView and Adapter
        binding.openOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OpenOrdersAdapter adapter = new OpenOrdersAdapter(new ArrayList<OpenOrderItem>(),this); // Pass empty list initially
        binding.openOrdersRecyclerView.setAdapter(adapter);

        openOrdersViewModel.getPendingOrders();

        // Observe the LiveData from the ViewModel
        openOrdersViewModel.getOrdersListLiveData().observe(getViewLifecycleOwner(), adapter::setOpenOrderItems);

    }

    @Override
    public void onEditOrderClick(Order order) {
        Toast.makeText(getContext(), "Edit Order Clicked", Toast.LENGTH_SHORT).show();
        cartViewModel.onLoadOpenOrderToCart(order);
        dismiss();
    }

    @Override
    public void onViewOrderClick(Order order) {
        Toast.makeText(getContext(), "View Order Clicked", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPrintReceiptClick(Order order) {
        Toast.makeText(getContext(), "Print Receipt Clicked", Toast.LENGTH_SHORT).show();
    }
}
