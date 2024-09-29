package com.example.ecommerce.features.order.open_orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.ecommerce.App;
import com.example.ecommerce.databinding.FragmentOpenOrdersBinding;
import com.example.ecommerce.features.order.OnLoadCartCallback;
import com.example.ecommerce.features.order.OnOrdersFetchedCallback;
import com.example.ecommerce.features.order.OrderViewModel;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.OpenOrderItem;
import com.example.ecommerce.model.Order;
import com.example.ecommerce.features.cart.CartViewModel;
import com.example.ecommerce.features.customers.CustomerViewModel;
import com.example.ecommerce.features.discount.DiscountViewModel;
import com.example.ecommerce.utils.OpenOrdersAdapter;

import java.util.ArrayList;

public class OpenOrdersFragment extends DialogFragment implements OnOpenOrderClickListener {

    public static final String TAG = "OpenOrdersFragment";
    private FragmentOpenOrdersBinding binding;
    private CartViewModel cartViewModel;
    private CustomerViewModel customerViewModel;
    private DiscountViewModel discountViewModel;
    private OrderViewModel orderViewModel;
    private OpenOrdersViewModel openOrdersViewModel;

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

        openOrdersViewModel = new ViewModelProvider(this, new OpenOrdersViewModelFactory(App.appModule.provideOrderRepository(),App.appModule.provideProductRepository())).get(OpenOrdersViewModel.class);
        cartViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);
        customerViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCustomerViewModelFactory()).get(CustomerViewModel.class);
        discountViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideDiscountViewModelFactory()).get(DiscountViewModel.class);
        orderViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideOrderViewModelFactory()).get(OrderViewModel.class);

        binding.closeButton.setOnClickListener(v -> dismiss());

        // Initialize RecyclerView and Adapter
        binding.openOrdersRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        OpenOrdersAdapter adapter = new OpenOrdersAdapter(new ArrayList<OpenOrderItem>(), this); // Pass empty list initially
        binding.openOrdersRecyclerView.setAdapter(adapter);

        orderViewModel.onLoadPendingOrders(new OnOrdersFetchedCallback() {
            @Override
            public void onOrdersFetched(ArrayList<Order> orders) {
                openOrdersViewModel.setPendingOrders(orders);
            }

            @Override
            public void onOrdersFetchFailed(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });

        // Observe the LiveData from the ViewModel
        openOrdersViewModel.getOrdersListLiveData().observe(getViewLifecycleOwner(), adapter::setOpenOrderItems);

    }

    @Override
    public void onEditOrderClick(Order order) {
        try{
            Toast.makeText(getContext(), "Edit Order Clicked", Toast.LENGTH_SHORT).show();
            openOrdersViewModel.onLoadPendingOrder(
                    order,
                    new OnLoadCartCallback() {
                        @Override
                        public void onLoadCart(Cart cartToLoad) {
                            cartViewModel.onLoadOpenOrderToCart(cartToLoad);
                        }
                    }
            );
            discountViewModel.onSetCurrentDiscount(order.getDiscountId());
            customerViewModel.onSetCurrentCustomer(order.getCustomerId());
            dismiss();
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error loading order", Toast.LENGTH_SHORT).show();
        }
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
