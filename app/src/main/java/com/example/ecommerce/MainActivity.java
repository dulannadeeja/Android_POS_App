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
import com.example.ecommerce.features.cart.CartFragment;
import com.example.ecommerce.features.cart.CartViewModel;
import com.example.ecommerce.features.cart.OnSavedPendingOrderCallback;
import com.example.ecommerce.features.checkout.CheckoutFragment;
import com.example.ecommerce.features.create_product.CreateProductFragment;
import com.example.ecommerce.features.customers.CustomerViewModel;
import com.example.ecommerce.features.customers.CustomersFragment;
import com.example.ecommerce.features.customers.customer_profile.CustomerProfileFragment;
import com.example.ecommerce.features.discount.DiscountPopupFragment;
import com.example.ecommerce.features.discount.DiscountViewModel;
import com.example.ecommerce.features.open_orders.OpenOrdersFragment;
import com.example.ecommerce.features.order.OrderViewModel;
import com.example.ecommerce.features.products.ProductsFragment;
import com.example.ecommerce.model.Cart;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.utils.DatabaseHelper;
import com.example.ecommerce.utils.OnCompletableFinishedCallback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static DrawerLayout drawerLayout;
    private CustomerViewModel customerViewModel;
    private CartViewModel cartViewModel;
    private DiscountViewModel discountViewModel;
    private OrderViewModel orderViewModel;

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
        discountViewModel = new ViewModelProvider(this, App.appModule.provideDiscountViewModelFactory()).get(DiscountViewModel.class);
        cartViewModel = new ViewModelProvider(this, App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);
        orderViewModel = new ViewModelProvider(this, App.appModule.provideOrderViewModelFactory()).get(OrderViewModel.class);

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
            } else if (item.getItemId() == R.id.clear_cart) {
                cartViewModel.onClearCart();
                customerViewModel.onClearCurrentCustomer();
                discountViewModel.onClearDiscount();
                return true;
            } else {
                return false;
            }
        });

        toolbar.findViewById(R.id.go_to_cart).setOnClickListener(v -> {
            loadFragment(new CartFragment(),true);
        });

        // by clicking outside the drawer it will close
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        binding.chargeButton.setOnClickListener(v -> {
            loadFragment(new CheckoutFragment(), true);
        });

        // ------------------------------------ Load the default fragment ------------------------------------
        if (savedInstanceState == null) {
            loadFragment(new ProductsFragment(), false);
        }

        // ------------------------------------ Database usage ------------------------------------
        DatabaseHelper database = App.appModule.provideDatabaseHelper();
//         database.onUpgrade(database.getWritableDatabase(), 1, 1);


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

                Cart cartToSave = cartViewModel.getCart().getValue();
                Customer customer = customerViewModel.getCustomer().getValue();

                binding.chargeButton.setEnabled(true);
                binding.chargeButton.setText("CHARGE " + cart.getCartTotalPrice());

                if(cart.isOpenOrder() && cart.getOrderId() != 0){
                    binding.saveOrderButton.setText("UPDATE");
                    binding.saveOrderButton.setOnClickListener(v -> {
                        orderViewModel.onUpdatePendingOrder(cartToSave, customer,
                                ((isSuccess, message) -> {
                                    if (isSuccess) {
                                        Toast.makeText(this, "Order updated successfully", Toast.LENGTH_SHORT).show();
                                        cartViewModel.onClearCart();
                                        customerViewModel.onClearCurrentCustomer();
                                        discountViewModel.onClearDiscount();
                                    } else {
                                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        );
                    });
                }else{
                    binding.saveOrderButton.setText("SAVE");
                    binding.saveOrderButton.setOnClickListener(v -> {
                        orderViewModel.onSavePendingOrder(cartToSave, customer,
                                ((isSuccess, message) -> {
                                    if (isSuccess) {
                                        Toast.makeText(this, "Order saved successfully", Toast.LENGTH_SHORT).show();
                                        cartViewModel.onClearCart();
                                        customerViewModel.onClearCurrentCustomer();
                                        discountViewModel.onClearDiscount();
                                    } else {
                                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                                    }
                                })
                        );
                    });
                }
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

    public void loadFragment(Fragment fragment, Boolean addToBackStack) {
        // Load the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        if(addToBackStack) {
            String name = fragment.getClass().getName();
            fragmentTransaction.addToBackStack(name);
        }
        fragmentTransaction.commit();

        // Set the active drawer item
        setActiveDrawerItem(fragment);
    }

    public void clearBackStack() {
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;

        // clear the back stack
        clearBackStack();

        if (item.getItemId() == R.id.drawer_products) {
            fragment = new ProductsFragment();
        } else if (item.getItemId() == R.id.drawer_create_product) {
            fragment = new CreateProductFragment();
        }

        if (fragment != null) {
            loadFragment(fragment, false);
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }

    public static void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}