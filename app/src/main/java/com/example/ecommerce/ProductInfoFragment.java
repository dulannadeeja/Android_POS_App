package com.example.ecommerce;

import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.ecommerce.databinding.FragmentProductInfoBinding;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.ui.create_product.CreateProductFragment;

public class ProductInfoFragment extends DialogFragment {

    private static final String ARG_PRODUCT = "product";
    private Product product;

    public static ProductInfoFragment newInstance(Product product) {
        ProductInfoFragment fragment = new ProductInfoFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            product = getArguments().getParcelable(ARG_PRODUCT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_product_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentProductInfoBinding binding = FragmentProductInfoBinding.bind(view);

        // Set the product details
        Uri productImage = Uri.parse(product.getProductImage());
        binding.productImage.setImageURI(productImage);
        binding.tvProductName.setText(product.getProductName());
        binding.tvProductStock.setText(String.format("%s in stock", String.valueOf(product.getProductQuantity())));
        binding.tvProductCode.setText(String.valueOf(product.get_productId()));
        binding.tvProductBrand.setText(product.getProductBrand());
        binding.tvProductPrice.setText(String.valueOf(product.getProductPrice()));
        binding.tvProductCost.setText(String.valueOf(product.getProductCost()));
        binding.tvProductDiscount.setText(String.valueOf(product.getProductDiscount()));
        binding.tvProductCategory.setText(product.getProductCategory());

        binding.editButton.setOnClickListener(v -> {
            // navigate to the create product fragment
            ((MainActivity) requireActivity()).loadFragment(CreateProductFragment.newInstance(product));
            // close the dialog
            dismiss();
        });
    }
}