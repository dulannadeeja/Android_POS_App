package com.example.ecommerce;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.checkout.CheckoutFragment;
import com.example.ecommerce.ui.products.ProductsFragment;
import com.example.ecommerce.ui.summary.SummaryFragment;
import com.example.ecommerce.utils.DatabaseHelper;

public class MainActivity extends AppCompatActivity {

    public static DatabaseHelper databaseHelper;

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

        // Load the default fragment
        if(savedInstanceState == null) {
            loadFragment(new ProductsFragment());
        }

        // Initialize the database helper
        databaseHelper = new DatabaseHelper(this);

        // Initialize the database
        databaseHelper.getWritableDatabase();
    }

    public void loadFragment(Fragment fragment) {
        // Load the fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();
    }
}