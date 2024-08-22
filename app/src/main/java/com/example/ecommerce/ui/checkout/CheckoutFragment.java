package com.example.ecommerce.ui.checkout;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.dao.CartDao;
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.ICartDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.databinding.FragmentCheckoutBinding;
import com.example.ecommerce.repository.CartRepository;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.ICartRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.cart.CartViewModel;
import com.example.ecommerce.ui.cart.CartViewModelFactory;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.ui.summary.SummaryFragment;
import com.example.ecommerce.utils.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;

public class CheckoutFragment extends Fragment {

    private DatabaseHelper databaseHelper;
    private ICartDao cartDao;
    private ICartRepository cartRepository;
    private IDiscountDao discountDao;
    private IDiscountRepository discountRepository;
    private CartViewModel cartViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
                ((MainActivity) getActivity()).loadFragment(new CartFragment());
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_checkout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCheckoutBinding binding = FragmentCheckoutBinding.bind(view);

        MaterialToolbar toolbar = view.findViewById(R.id.checkout_toolbar);
        toolbar.inflateMenu(R.menu.menu_checkout_appbar);
        toolbar.setTitle("Payment");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d("ProductsFragment", "onOptionsItemSelected");
            if (item.getItemId() == R.id.new_user) {
                Toast.makeText(getContext(), "New user clicked", Toast.LENGTH_SHORT).show();
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
                ((MainActivity) getActivity()).loadFragment(new CartFragment());
            }
        });

        // Initialize the database helper and DAO objects for the cart
        databaseHelper = MainActivity.databaseHelper;
        cartDao = new CartDao(databaseHelper);
        cartRepository = new CartRepository(cartDao);

        discountDao = new DiscountDao(databaseHelper);
        discountRepository = new DiscountRepository(discountDao);

        // Initialize the cart view model
        cartViewModel = new ViewModelProvider(this, new CartViewModelFactory(cartRepository,discountRepository)).get(CartViewModel.class);

        // Fetch cart
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            binding.tvTotalAmount.setText(String.valueOf(cart.getCartTotalPrice()));
        });

        binding.etAmountDue.setText(binding.tvTotalAmount.getText());

        binding.chargeButton.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Payment successful", Toast.LENGTH_SHORT).show();
            // load summary fragment
            ((MainActivity) requireActivity()).loadFragment(new SummaryFragment());
        });
    }
}