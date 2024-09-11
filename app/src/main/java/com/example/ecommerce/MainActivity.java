package com.example.ecommerce;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.ecommerce.databinding.ActivityMainBinding;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.cart.CartViewModel;
import com.example.ecommerce.ui.cart.OnSavedPendingOrderCallback;
import com.example.ecommerce.ui.checkout.CheckoutFragment;
import com.example.ecommerce.ui.create_product.CreateProductFragment;
import com.example.ecommerce.ui.customers.CustomerViewModel;
import com.example.ecommerce.ui.customers.CustomersFragment;
import com.example.ecommerce.ui.customers.OnCurrentCustomerChangedCallback;
import com.example.ecommerce.ui.customers.customer_profile.CustomerProfileFragment;
import com.example.ecommerce.ui.discount.DiscountPopupFragment;
import com.example.ecommerce.ui.discount.DiscountViewModel;
import com.example.ecommerce.ui.open_orders.OpenOrdersFragment;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.utils.DatabaseHelper;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static DrawerLayout drawerLayout;
    private CustomerViewModel customerViewModel;
    private CartViewModel cartViewModel;
    private DiscountViewModel discountViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize the view model
        customerViewModel = new ViewModelProvider(this, App.appModule.provideCustomerViewModelFactory()).get(CustomerViewModel.class);
        cartViewModel = new ViewModelProvider(this, App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);
        discountViewModel = new ViewModelProvider(this, App.appModule.provideDiscountViewModelFactory()).get(DiscountViewModel.class);

        // Load the current customer
        customerViewModel.onLoadCurrentCustomer();

        // Set up the Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // ------------------------------------ Setup toolbar ------------------------------------
        MaterialToolbar toolbar = findViewById(R.id.main_toolbar);
        toolbar.inflateMenu(R.menu.menu_main_appbar);
        toolbar.setNavigationIcon(R.drawable.baseline_menu_24);
        toolbar.setNavigationOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        TextView toolbarTvCartItemCount = toolbar.findViewById(R.id.item_count);
        toolbarTvCartItemCount.setText("0");

        // Setup initial toolbar menu items click listeners
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.new_user) {
                return true;
            } else if (item.getItemId() == R.id.discount) {
                // Show the discount popup fragment
                DiscountPopupFragment popupFragment = new DiscountPopupFragment();
                popupFragment.show(getSupportFragmentManager(), "DiscountPopupFragment");
                return true;
            } else if (item.getItemId() == R.id.settings) {
                return true;
            } else {
                return false;
            }
        });

        toolbar.findViewById(R.id.go_to_cart).setOnClickListener(v -> {
            loadFragment(new CartFragment());
        });

        // by clicking outside the drawer it will close
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        binding.chargeButton.setOnClickListener(v -> {
            loadFragment(new CheckoutFragment());
        });

        // ------------------------------------ Load the default fragment ------------------------------------
        if (savedInstanceState == null) {
            loadFragment(new ProductsFragment());
        }

        // ------------------------------------ Database usage ------------------------------------
        DatabaseHelper database = App.appModule.provideDatabaseHelper();
        // database.onUpgrade(database.getWritableDatabase(), 1, 1);


        // ------------------------------------ Example of firebase database usage ------------------------------------
        FirebaseDatabase Fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = Fdatabase.getReference("message");
        myRef.setValue("Hello, World!");

        // ------------------------------------ Observers -------------------------------------------------------------

        // Observe the current customer
        customerViewModel.getCustomer().observe(this, customer -> {
            MenuItem newUserMenuItem = toolbar.getMenu().findItem(R.id.new_user);
            if (customer != null && customer.getCustomerId() != 0) {
                newUserMenuItem.setIcon(R.drawable.baseline_person_add_24);
                newUserMenuItem.setOnMenuItemClickListener(item -> {
                    CustomerProfileFragment customerProfileFragment = CustomerProfileFragment.newInstance(customer);
                    customerProfileFragment.show(getSupportFragmentManager(), CustomerProfileFragment.TAG);
                    return true;
                });
            } else {
                newUserMenuItem.setIcon(R.drawable.baseline_person_add_alt_24);
                newUserMenuItem.setOnMenuItemClickListener(item -> {
                    CustomersFragment customersFragment = new CustomersFragment();
                    customersFragment.show(getSupportFragmentManager(), CustomersFragment.TAG);
                    return true;
                });
            }
        });

        cartViewModel.getCart().observe(this, cart -> {
            toolbarTvCartItemCount.setText(String.valueOf(cart.getTotalItems()));
            if (cart.getCartItems().isEmpty()) {
                // disable the charge button
                binding.chargeButton.setEnabled(false);
                binding.chargeButton.setText("CHARGE");
                binding.saveOrderButton.setText("OPEN ORDERS");
                binding.saveOrderButton.setOnClickListener(v -> {
                    OpenOrdersFragment openOrdersFragment = new OpenOrdersFragment();
                    openOrdersFragment.show(getSupportFragmentManager(), OpenOrdersFragment.TAG);
                });
            } else {
                binding.chargeButton.setEnabled(true);
                binding.chargeButton.setText("CHARGE " + cart.getCartTotalPrice());
                binding.saveOrderButton.setText("SAVE");
                binding.saveOrderButton.setOnClickListener(v -> {
                    cartViewModel.onSavePendingOrder(new OnSavedPendingOrderCallback() {
                        @Override
                        public void onSuccessfulOrderSaved() {
                            Toast.makeText(MainActivity.this, "Order saved successfully", Toast.LENGTH_SHORT).show();
                            cartViewModel.onClearCart();
                            customerViewModel.onClearCurrentCustomer();
                            customerViewModel.onClearCustomer();
                        }

                        @Override
                        public void onFailedOrderSaved(String message) {
                            Toast.makeText(MainActivity.this, "Failed to save order: " + message, Toast.LENGTH_SHORT).show();
                        }
                    });
                });
            }
        });

        discountViewModel.getDiscountValue().observe(this, discountValue -> {
            if (discountValue == null || discountValue == 0) {
                toolbar.getMenu().findItem(R.id.discount).setIcon(R.drawable.tag);
            } else {
                toolbar.getMenu().findItem(R.id.discount).setIcon(R.drawable.tag_filled);
            }
        });
    }

    public void setActiveDrawerItem(Fragment fragment) {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        if (fragment instanceof ProductsFragment) {
            navigationView.setCheckedItem(R.id.drawer_products);
        } else if (fragment instanceof CreateProductFragment) {
            navigationView.setCheckedItem(R.id.drawer_create_product);
        }

    }

    public void loadFragment(Fragment fragment) {
        // Load the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        // Set the active drawer item
        setActiveDrawerItem(fragment);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        if (item.getItemId() == R.id.drawer_products) {
            fragment = new ProductsFragment();
        } else if (item.getItemId() == R.id.drawer_create_product) {
            fragment = new CreateProductFragment();
        }

        if (fragment != null) {
            loadFragment(fragment);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public static void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}