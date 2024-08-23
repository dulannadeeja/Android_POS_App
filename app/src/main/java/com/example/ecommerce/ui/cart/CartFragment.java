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
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.ui.discount.DiscountPopupFragment;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.databinding.FragmentCartBinding;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.ui.checkout.CheckoutFragment;
import com.example.ecommerce.ui.discount.DiscountViewModel;
import com.example.ecommerce.ui.discount.DiscountViewModelFactory;
import com.example.ecommerce.ui.products.OnItemClickListener;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.utils.CartItemsAdapter;
import com.example.ecommerce.utils.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class CartFragment extends Fragment implements OnItemClickListener {

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

        MaterialToolbar toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_main_appbar);
        toolbar.findViewById(R.id.go_to_cart).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new CheckoutFragment());
        });
        TextView tvItemCount = toolbar.findViewById(R.id.item_count);
        TextView tvGoToCart = toolbar.findViewById(R.id.go_to_cart);
        tvGoToCart.setVisibility(View.GONE);
        toolbar.setTitle("Cart");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.share) {
                Toast.makeText(getContext(), "Share clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate somewhere.
                ((MainActivity) getActivity()).loadFragment(new ProductsFragment());
            }
        });
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d("ProductsFragment", "onOptionsItemSelected");
            if (item.getItemId() == R.id.new_user) {
                Toast.makeText(getContext(), "New user clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else if (item.getItemId() == R.id.categories) {
                // Show the popup fragment
                    DiscountPopupFragment popupFragment = new DiscountPopupFragment();
                    popupFragment.show(getParentFragmentManager(), "DiscountPopupFragment");
                return true;
            } else if (item.getItemId() == R.id.settings) {
                Toast.makeText(getContext(), "Settings clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        binding.chargeButton.setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new CheckoutFragment());
        });

        // Initialize the cart view model
        cartViewModel = new ViewModelProvider(this, App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);

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
            tvItemCount.setText(String.valueOf(cart.getTotalItems()));
            binding.tvTaxAmount.setText(String.valueOf(cart.getCartTotalTax()));
            binding.tvTotalAmount.setText(String.valueOf(cart.getCartTotalPrice()));
            binding.tvSubTotalAmount.setText(String.valueOf(cart.getCartSubTotalPrice()));
            binding.tvDiscountAmount.setText(String.valueOf(cart.getDiscountValue()));
            if(cart.getDiscountValue() > 0) {
                binding.discountContainer.setVisibility(View.VISIBLE);
            } else {
                binding.discountContainer.setVisibility(View.GONE);
            }
            if(cart.getCartItems().isEmpty()) {
                // disable the charge button
                binding.chargeButton.setEnabled(false);
                binding.chargeButton.setText("CHARGE");

            } else {
                binding.chargeButton.setEnabled(true);
                binding.chargeButton.setText("CHARGE " + cart.getCartTotalPrice());

            }
        });

        discountViewModel.getDiscount().observe(getViewLifecycleOwner(), discount -> {
            Log.d(TAG, "discount has been changed: " + discount);
            cartViewModel.setDiscount();
        });
    }

    @Override
    public void onItemClick(int cartItemId) {
        Toast.makeText(getContext(), "Cart item clicked" + cartItemId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemLongClick(int cartItemId) {
        cartViewModel.onRemoveFromCart(cartItemId);
        Toast.makeText(getContext(), "Cart item removed" + cartItemId, Toast.LENGTH_SHORT).show();
    }
}