package com.example.ecommerce.features.products;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ecommerce.App;
import com.example.ecommerce.model.CartItem;
import com.example.ecommerce.features.cart.OnCartOperationCompleted;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.features.cart.CartViewModel;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentProductsBinding;
import com.example.ecommerce.utils.ProductsAdapter;

import java.util.ArrayList;

public class ProductsFragment extends Fragment implements OnItemClickListener {

    private static final String TAG = "ProductsFragment";
    private ProductsViewModel productsViewModel;
    private CartViewModel cartViewModel;

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

        // Initialize ViewModel with repository
        productsViewModel = new ViewModelProvider(this, App.appModule.provideProductsViewModelFactory()).get(ProductsViewModel.class);

        productsViewModel.setCustomer();

        // Initialize CartViewModel with repository
        cartViewModel = new ViewModelProvider(this, App.appModule.provideCartViewModelFactory()).get(CartViewModel.class);

        // Initialize RecyclerView and Adapter
        RecyclerView productsRecyclerView = view.findViewById(R.id.products_recycler_view);
        productsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ProductsAdapter adapter = new ProductsAdapter(this, getContext(), new ArrayList<Product>()); // Pass empty list initially
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
                    if (products == null) {
                        return;
                    }
                    adapter.setProducts(products);
                });

        cartViewModel.getCart().observe(getViewLifecycleOwner(), cart -> {
            if (cart != null) {
                ArrayList<CartItem> cartItems = cart.getCartItems();
                cartItems.forEach(cartItem -> {
                    adapter.setProductQuantity(cartItem.getProductId(), cartItem.getQuantity());
                });
            }else{
                adapter.clearProductQuantity();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        productsViewModel.setCustomer();
    }



    @Override
    public void onItemClick(int productId) {
        cartViewModel.onAddToCart(productId, new OnCartOperationCompleted() {
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
    public void onItemLongClick(int productId) {
        cartViewModel.onRemoveFromCart(productId);
    }

    @Override
    public void onProductInfoClick(Product product) {
        ProductInfoFragment productInfoFragment = ProductInfoFragment.newInstance(product);
        productInfoFragment.show(getParentFragmentManager(), "ProductInfoFragment");
    }

}
