package com.example.ecommerce.ui.products;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.App;
import com.example.ecommerce.ui.cart.OnSavedPendingOrderCallback;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.cart.CartViewModel;
import com.example.ecommerce.ui.checkout.CheckoutFragment;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentProductsBinding;
import com.example.ecommerce.ui.customers.CustomersFragment;
import com.example.ecommerce.ui.discount.DiscountPopupFragment;
import com.example.ecommerce.ui.open_orders.OpenOrdersFragment;
import com.example.ecommerce.utils.ProductsAdapter;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;

public class ProductsFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "ProductsFragment";
    private ProductsViewModel productsViewModel;
    private CartViewModel cartViewModel;
    private static MaterialToolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_products, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentProductsBinding binding = FragmentProductsBinding.bind(view);

        toolbar = view.findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_main_appbar);
        toolbar.findViewById(R.id.go_to_cart).setOnClickListener(v -> {
            ((MainActivity) requireActivity()).loadFragment(new CartFragment());
        });
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);
        toolbar.setNavigationOnClickListener(v -> {
            ((MainActivity) requireActivity()).openDrawer();
        });
        TextView tvItemCount = toolbar.findViewById(R.id.item_count);
        toolbar.setOnMenuItemClickListener(item -> {
            Log.d("ProductsFragment", "onOptionsItemSelected");
            if (item.getItemId() == R.id.new_user) {
                onAddCustomerClick();
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

        // Initialize ViewModel with repository
        productsViewModel = new ViewModelProvider(this, App.appModule.provideProductsViewModelFactory()).get(ProductsViewModel.class);

        // Initialize CartViewModel with repository
        cartViewModel = new ViewModelProvider(this, App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);

        // Initialize RecyclerView and Adapter
        RecyclerView productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductsAdapter adapter = new ProductsAdapter(this,getContext(),new ArrayList<Product>()); // Pass empty list initially
        productsRecyclerView.setAdapter(adapter);

        // Fetch data from the API
        productsViewModel.setProducts();

        // Set up search bar
        binding.searchProduct.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                productsViewModel.onFilterProducts(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productsViewModel.onFilterProducts(newText);
                return false;
            }
        });

        // Observe productsViewModel LiveData
        productsViewModel.getErrorMessage().observe(getViewLifecycleOwner(), errorMessage -> {
            if (!errorMessage.isEmpty()) {
                Toast.makeText(getContext(), errorMessage, Toast.LENGTH_SHORT).show();
            }
        });

        // Update RecyclerView adapter's data
        productsViewModel.getProducts().observe(getViewLifecycleOwner(),
                products -> {
            if(products == null) {
                return;
            }
            adapter.setProducts(products);
        });

        // Observe CartViewModel LiveData
        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            tvItemCount.setText(String.valueOf(cart.getTotalItems()));
            if(cart.getCartItems().isEmpty()) {
                // disable the charge button
                binding.chargeButton.setEnabled(false);
                binding.chargeButton.setText("CHARGE");
                binding.saveOrderButton.setText("OPEN ORDERS");
                binding.saveOrderButton.setOnClickListener(v->{
                    OpenOrdersFragment openOrdersFragment = new OpenOrdersFragment();
                    openOrdersFragment.show(getParentFragmentManager(), "OpenOrdersFragment");
                });
            } else {
                binding.chargeButton.setEnabled(true);
                binding.chargeButton.setText("CHARGE " + cart.getCartTotalPrice());
                binding.saveOrderButton.setText("SAVE");
                binding.saveOrderButton.setOnClickListener(v -> {
                    cartViewModel.onSavePendingOrder(new OnSavedPendingOrderCallback() {
                        @Override
                        public void onSuccessfulOrderSaved() {
                            Toast.makeText(getContext(), "Order saved successfully", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onFailedOrderSaved(String message) {
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });
    }

    @Override
    public void onItemClick(int productId) {
        cartViewModel.onAddToCart(productId);
    }

    @Override
    public void onItemLongClick(int productId) {
        cartViewModel.onRemoveFromCart(productId);
    }

    @Override
    public void onProductInfoClick(Product product) {
        ProductInfoFragment productInfoFragment = ProductInfoFragment.newInstance(product);
        productInfoFragment.show(getParentFragmentManager(), "ProductInfoFragment");
    }

    @Override
    public void onAddCustomerClick() {
        CustomersFragment customersFragment = new CustomersFragment();
        customersFragment.show(getParentFragmentManager(), "CustomersFragment");
    }

    public static void markAddCustomerIconAsActive(){
        // Change the icon to a check mark
        toolbar.getMenu().findItem(R.id.new_user).setIcon(R.drawable.baseline_person_add_24);
    }

    public static void markAddCustomerIconAsInactive(){
        // Change the icon to a plus sign
        toolbar.getMenu().findItem(R.id.new_user).setIcon(R.drawable.baseline_person_add_alt_24);
    }

}
