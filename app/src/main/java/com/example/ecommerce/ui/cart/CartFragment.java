package com.example.ecommerce.ui.cart;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.ui.discount.DiscountPopupFragment;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentCartBinding;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.ui.checkout.CheckoutFragment;
import com.example.ecommerce.ui.discount.DiscountViewModel;
import com.example.ecommerce.ui.products.OnItemClickListener;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.utils.CartItemsAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class CartFragment extends Fragment implements OnCartItemClickListener {

    private static final String TAG = "CartFragment";
    private CartViewModel cartViewModel;
    private DiscountViewModel discountViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity) getActivity()).loadFragment(new ProductsFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_cart, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCartBinding binding = FragmentCartBinding.bind(view);

        MaterialToolbar toolbar = getActivity().findViewById(R.id.main_toolbar);
        TextView tvGoToCart = toolbar.findViewById(R.id.go_to_cart);
        tvGoToCart.setVisibility(View.GONE);
        toolbar.setTitle("Cart");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24_white);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate somewhere.
                ((MainActivity) getActivity()).loadFragment(new ProductsFragment());
            }
        });

        // Initialize the cart view model
        cartViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);

        // Initialize the discount view model
        discountViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideDiscountViewModelFactory()).get(DiscountViewModel.class);

        // Initialize the cart items adapter
        CartItemsAdapter cartItemsAdapter = new CartItemsAdapter(this, getContext(), new ArrayList<CartItem>());
        binding.cartItemsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.cartItemsRecyclerView.setAdapter(cartItemsAdapter);

        // Fetch cart
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            Log.d(TAG, "cart has been changed: " + cart);
            cartItemsAdapter.setCartItems(cart.getCartItems());
            binding.tvTaxAmount.setText(String.valueOf(cart.getCartTotalTax()));
            binding.tvTotalAmount.setText(String.valueOf(cart.getCartTotalPrice()));
            binding.tvSubTotalAmount.setText(String.valueOf(cart.getCartSubTotalPrice()));
            binding.tvDiscountAmount.setText(String.valueOf(cart.getDiscountValue()));
            if(cart.getDiscountValue() > 0) {
                binding.discountContainer.setVisibility(View.VISIBLE);
            } else {
                binding.discountContainer.setVisibility(View.GONE);
            }
        });

        discountViewModel.getDiscount().observe(getViewLifecycleOwner(), discount -> {
            Log.d(TAG, "discount has been changed: " + discount);
            cartViewModel.setDiscount();
        });
    }

    @Override
    public void onCartItemClick(int cartItemId) {
        // TODO: Implement this method
    }

    @Override
    public void onCartItemLongClick(int cartItemId) {
        cartViewModel.onRemoveFromCart(cartItemId);
    }

    @Override
    public void onCartItemAddClick(int cartItemId) {
        cartViewModel.onAddToCart(cartItemId, new OnCartOperationCompleted() {
            @Override
            public void onSuccessfulCartOperation() {
                Toast.makeText(getContext(), "Item added to cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedCartOperation(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onCartItemRemoveClick(int cartItemId) {
        cartViewModel.onDecrementProductQuantity(cartItemId, new OnCartOperationCompleted() {
            @Override
            public void onSuccessfulCartOperation() {
                Toast.makeText(getContext(), "Item removed from cart", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailedCartOperation(String message) {
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        // Restore the toolbar state when leaving the fragment
        MaterialToolbar toolbar = getActivity().findViewById(R.id.main_toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MainActivity.openDrawer();
            }
        });
        toolbar.setTitle("");
        TextView tvGoToCart = toolbar.findViewById(R.id.go_to_cart);
        tvGoToCart.setVisibility(View.VISIBLE);

        // Clear the cart view model
        cartViewModel.dispose();
    }
}