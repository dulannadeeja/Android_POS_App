package com.example.ecommerce.ui.create_product;

import android.app.Application;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.dao.IProductDao;
import com.example.ecommerce.databinding.FragmentCreateProductBinding;
import com.example.ecommerce.model.Product;
import com.example.ecommerce.repository.IProductRepository;
import com.example.ecommerce.ui.cart.CartFragment;
import com.example.ecommerce.ui.discount.DiscountPopupFragment;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.Objects;

public class CreateProductFragment extends Fragment {

    private static final String TAG = "CreateProductFragment";
    private CreateProductViewModel createProductViewModel;
    private static final String ARG_PRODUCT = "product";
    private Product product;
    private Boolean isEdit = false;

    public static CreateProductFragment newInstance(Product product) {
        CreateProductFragment  fragment = new CreateProductFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_PRODUCT, product);
        fragment.setArguments(args);
        fragment.isEdit = product != null;
        return fragment;
    }

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

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
        return inflater.inflate(R.layout.fragment_create_product, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCreateProductBinding binding = FragmentCreateProductBinding.bind(view);

        createProductViewModel = new ViewModelProvider(this, new CreateProductViewModelFactory(App.appModule.provideProductRepository(),product)).get(CreateProductViewModel.class);

        ((MainActivity) requireActivity()).findViewById(R.id.actions_container).setVisibility(View.GONE);

        // handle product name change
        binding.etProductName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productName", s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product brand change
        binding.brandAutoComplete.setOnItemClickListener((adapterView, view1, i, l) -> {
            String brand = (String) adapterView.getItemAtPosition(i);
            createProductViewModel.applyUpdateToTheProduct("productBrand", brand);
        });

        binding.brandAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productBrand", s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product category change
        binding.radioGroupCategory.setOnCheckedChangeListener((radioGroup, i) -> {
            if (i == R.id.radio_category_men) {
                createProductViewModel.applyUpdateToTheProduct("productCategory", "men");
            } else if (i == R.id.radio_category_women) {
                createProductViewModel.applyUpdateToTheProduct("productCategory", "women");
            } else if (i == R.id.radio_category_kids) {
                createProductViewModel.applyUpdateToTheProduct("productCategory", "kids");
            } else if (i == R.id.radio_category_accessories) {
                createProductViewModel.applyUpdateToTheProduct("productCategory", "accessories");
            }
        });

        // handle product description change
        binding.etProductDescription.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productDescription", s.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product price change
        binding.etProductPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().isEmpty()) {
                    s = "-1";
                }

                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productPrice", Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product cost change
        binding.etProductCost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().isEmpty()) {
                    s = "-1";
                }

                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productCost", Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product discount change
        binding.etProductDiscount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().isEmpty()) {
                    s = "0";
                }

                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productDiscount", Double.parseDouble(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }

        });

        // handle product quantity change
        binding.etProductStock.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    s = "-1";
                }
                // Handle text change
                createProductViewModel.applyUpdateToTheProduct("productQuantity", Integer.parseInt(s.toString()));
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        // handle product brand autocomplete
        String[] brands = getResources().getStringArray(R.array.brands);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, brands);
        binding.brandAutoComplete.setAdapter(adapter);

        // handle product image selection
        // Initialize the ActivityResultLauncher
        pickMediaLauncher = registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
            if (uri != null) {
                Log.d(TAG, "Selected URI: " + uri);
                createProductViewModel.saveImage(uri);
                binding.imagePreview.setImageURI(uri);
            } else {
                Log.d(TAG, "No media selected");
                binding.imagePreview.setImageResource(R.drawable.image_placeholder);
                createProductViewModel.deleteImage();
                createProductViewModel.applyUpdateToTheProduct("productImage", null);
            }
        });

        // handle image preview click
        binding.imagePreview.setOnClickListener(v -> {
            pickMediaLauncher.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });

        // handle remove image button
        binding.floatingActionButtonRemoveImage.setOnClickListener(v -> {
            binding.imagePreview.setImageResource(R.drawable.image_placeholder);
            createProductViewModel.deleteImage();
            createProductViewModel.applyUpdateToTheProduct("productImage", null);
        });

        // if the image is not selected hide the remove image button
        createProductViewModel.getProduct().observe(getViewLifecycleOwner(), product -> {
            if (product.getProductImage() == null) {
                binding.floatingActionButtonRemoveImage.setVisibility(View.GONE);
            } else {
                binding.floatingActionButtonRemoveImage.setVisibility(View.VISIBLE);
            }
        });

        // set the view based on whether the product is being edited or created
        if (isEdit) {
            binding.btnCreateProduct.setText("Update Product");
            binding.tvCreateProductTitle.setText("Edit Existing Product");
            binding.tvCreateProductSubtitle.setText("Please update the product details to save changes.");
        } else {
            binding.btnCreateProduct.setText("Create Product");
        }

        // handle create product button click
        binding.btnCreateProduct.setOnClickListener(v -> {
            if(!isEdit){
                createProductViewModel.createProduct(new OnProductCreatedCallback() {
                    @Override
                    public void onSuccessfulProductCreation() {
                        // Handle success
                        Toast.makeText(App.appModule.provideAppContext(), "Product created successfully!", Toast.LENGTH_SHORT).show();
                        // Clear the form
                        binding.etProductName.setText("");
                        binding.brandAutoComplete.setText("");
                        binding.etProductDescription.setText("");
                        binding.etProductPrice.setText("");
                        binding.etProductCost.setText("");
                        binding.etProductDiscount.setText("");
                        binding.etProductStock.setText("");
                        binding.imagePreview.setImageResource(R.drawable.image_placeholder);
                        binding.radioGroupCategory.clearCheck();
                        createProductViewModel.clearErrors();
                    }

                    @Override
                    public void onFailedProductCreation() {
                        // Handle failure
                        Toast.makeText(App.appModule.provideAppContext(), "Failed to create product. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                createProductViewModel.updateProduct(new OnProductUpdatedCallback() {
                    @Override
                    public void onProductUpdateSuccess() {
                        // Handle success
                        Toast.makeText(App.appModule.provideAppContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProductUpdateFailure() {
                        // Handle failure
                        Toast.makeText(App.appModule.provideAppContext(), "Failed to update product. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

        // Observe errors
        createProductViewModel.getProductNameError().observe(getViewLifecycleOwner(), binding.etProductName::setError);
        createProductViewModel.getProductBrandError().observe(getViewLifecycleOwner(), binding.tvBrandError::setText);
        createProductViewModel.getProductCategoryError().observe(getViewLifecycleOwner(), binding.tvCategoryError::setText);
        createProductViewModel.getProductDescriptionError().observe(getViewLifecycleOwner(), binding.etProductDescription::setError);
        createProductViewModel.getProductPriceError().observe(getViewLifecycleOwner(), binding.etProductPrice::setError);
        createProductViewModel.getProductCostError().observe(getViewLifecycleOwner(), binding.etProductCost::setError);
        createProductViewModel.getProductDiscountError().observe(getViewLifecycleOwner(), binding.etProductDiscount::setError);
        createProductViewModel.getProductQuantityError().observe(getViewLifecycleOwner(), binding.etProductStock::setError);

        if (product != null) {
            binding.etProductName.setText(product.getProductName());
            binding.brandAutoComplete.setText(product.getProductBrand());
            binding.etProductDescription.setText(product.getProductDescription());
            binding.etProductPrice.setText(String.valueOf(product.getProductPrice()));
            binding.etProductCost.setText(String.valueOf(product.getProductCost()));
            binding.etProductDiscount.setText(String.valueOf(product.getProductDiscount()));
            binding.etProductStock.setText(String.valueOf(product.getProductQuantity()));

            if(product.getProductImage() != null) {
                binding.imagePreview.setImageURI(Uri.parse(product.getProductImage()));
            } else {
                binding.imagePreview.setImageResource(R.drawable.image_placeholder);
            }

            if(Objects.equals(product.getProductCategory(), "men")){
                binding.radioCategoryMen.setChecked(true);
            } else if(Objects.equals(product.getProductCategory(), "women")){
                binding.radioCategoryWomen.setChecked(true);
            } else if(Objects.equals(product.getProductCategory(), "kids")){
                binding.radioCategoryKids.setChecked(true);
            } else if(Objects.equals(product.getProductCategory(), "accessories")){
                binding.radioCategoryAccessories.setChecked(true);
            }

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ((MainActivity) requireActivity()).findViewById(R.id.actions_container).setVisibility(View.VISIBLE);
    }
}