package com.example.ecommerce;

import android.os.Bundle;
import android.view.MenuItem;

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

import com.example.ecommerce.ui.create_product.CreateProductFragment;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.utils.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

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

        // Set up the Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Load the default fragment
        if (savedInstanceState == null) {
            loadFragment(new ProductsFragment());
        }

        // update the database
        DatabaseHelper database = App.appModule.provideDatabaseHelper();
//        database.onUpgrade(database.getWritableDatabase(), 1, 1);


        FirebaseDatabase Fdatabase = FirebaseDatabase.getInstance();
        DatabaseReference myRef = Fdatabase.getReference("message");

        myRef.setValue("Hello, World!");
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

        return true;
    }

    public void openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START);
    }
}