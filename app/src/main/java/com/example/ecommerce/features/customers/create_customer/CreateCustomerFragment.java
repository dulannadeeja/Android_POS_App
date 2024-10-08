package com.example.ecommerce.features.customers.create_customer;

import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentCreateCustomerBinding;
import com.example.ecommerce.model.Customer;
import com.example.ecommerce.features.customers.CustomerViewModel;
import com.example.ecommerce.features.customers.CustomersFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class CreateCustomerFragment extends DialogFragment {
    public static final String TAG = "CreateCustomerFragment";
    private CreateCustomerViewModel createCustomerViewModel;
    private CustomerViewModel customerViewModel;
    private Customer customer;
    private Boolean isEditMode = false;

    public static CreateCustomerFragment newInstance(Customer customer) {
        CreateCustomerFragment fragment = new CreateCustomerFragment();
        fragment.customer = customer;
        fragment.isEditMode = customer != null;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();

        // Set the dialog to full-screen width and height
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.white);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_customer, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentCreateCustomerBinding binding = FragmentCreateCustomerBinding.bind(view);

        createCustomerViewModel = new ViewModelProvider(this, App.appModule.provideCreateCustomerViewModelFactory()).get(CreateCustomerViewModel.class);
        customerViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideCustomerViewModelFactory()).get(CustomerViewModel.class);

        // Clear customer data when fragment is created
        customerViewModel.onClearCustomer();

        // Set is editing mode on view model
        createCustomerViewModel.setIsEditingMode(isEditMode);

        // if customer is not null, load customer data
        if (isEditMode) {
            customerViewModel.onLoadCustomer(customer.getCustomerId());
        }

        binding.ivAvatar.setOnClickListener(v -> {
            BottomSheetDialogFragment bottomSheetDialogFragment = ChoosePhotoBottomSheetFragment.newInstance(new OnPhotoChoosedCallback() {
                @Override
                public void onSuccessfulPhotoChoose(Uri uri) {
                    binding.ivAvatar.setImageURI(uri);
                    customerViewModel.applyUpdateToCustomer("photo", uri.toString());
                }

                @Override
                public void onFailedPhotoChoose(String message) {
                    binding.ivAvatar.setImageResource(R.drawable.avatar);
                    customerViewModel.applyUpdateToCustomer("photo", null);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPhotoRemoved() {
                    binding.ivAvatar.setImageResource(R.drawable.avatar);
                    customerViewModel.applyUpdateToCustomer("photo", null);
                }
            });
            bottomSheetDialogFragment.show(getChildFragmentManager(), bottomSheetDialogFragment.getTag());
        });

        // Form fields value change listeners
        binding.etFirstName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateFirstName(s.toString());
                customerViewModel.applyUpdateToCustomer("firstName", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etLastName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateLastName(s.toString());
                customerViewModel.applyUpdateToCustomer("lastName", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateEmail(s.toString());
                customerViewModel.applyUpdateToCustomer("email", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validatePhone(s.toString());
                customerViewModel.applyUpdateToCustomer("phone", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateAddress(s.toString());
                customerViewModel.applyUpdateToCustomer("address", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etCity.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateCity(s.toString());
                customerViewModel.applyUpdateToCustomer("city", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.etRegion.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                createCustomerViewModel.validateRegion(s.toString());
                customerViewModel.applyUpdateToCustomer("region", s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binding.radioGroupGender.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radio_button_male) {
                customerViewModel.applyUpdateToCustomer("gender", "male");
                createCustomerViewModel.validateGender("male");
            } else if (checkedId == R.id.radio_button_female) {
                customerViewModel.applyUpdateToCustomer("gender", "female");
                createCustomerViewModel.validateGender("female");
            } else if (checkedId == R.id.radio_button_other) {
                customerViewModel.applyUpdateToCustomer("gender", "other");
                createCustomerViewModel.validateGender("other");
            }
        });

        // ensure none gender selected by default
        binding.radioGroupGender.clearCheck();

        // Observe errors
        createCustomerViewModel.getFirstNameError().observe(getViewLifecycleOwner(), binding.etFirstName::setError);
        createCustomerViewModel.getLastNameError().observe(getViewLifecycleOwner(), binding.etLastName::setError);
        createCustomerViewModel.getEmailError().observe(getViewLifecycleOwner(), binding.etEmail::setError);
        createCustomerViewModel.getPhoneError().observe(getViewLifecycleOwner(), binding.etPhone::setError);
        createCustomerViewModel.getAddressError().observe(getViewLifecycleOwner(), binding.etAddress::setError);
        createCustomerViewModel.getCityError().observe(getViewLifecycleOwner(), binding.etCity::setError);
        createCustomerViewModel.getRegionError().observe(getViewLifecycleOwner(), binding.etRegion::setError);
        createCustomerViewModel.getGenderError().observe(getViewLifecycleOwner(), binding.tvGenderError::setText);

        if (isEditMode) {
            binding.etFirstName.setText(customer.getFirstName());
            binding.etLastName.setText(customer.getLastName());
            binding.etEmail.setText(customer.getEmail());
            binding.etPhone.setText(customer.getPhone());
            binding.etAddress.setText(customer.getAddress());
            binding.etCity.setText(customer.getCity());
            binding.etRegion.setText(customer.getRegion());
            if (customer.getPhoto() != null) {
                binding.ivAvatar.setImageURI(Uri.parse(customer.getPhoto()));
            }
            if (customer.getGender() != null) {
                switch (customer.getGender()) {
                    case "male":
                        binding.radioGroupGender.check(R.id.radio_button_male);
                        break;
                    case "female":
                        binding.radioGroupGender.check(R.id.radio_button_female);
                        break;
                    case "other":
                        binding.radioGroupGender.check(R.id.radio_button_other);
                        break;
                }
            }
        }


        createCustomerViewModel.getIsEditingMode().observe(getViewLifecycleOwner(), isEditingMode -> {
            if (isEditingMode) {
                binding.btnSave.setText("UPDATE");
            } else {
                binding.btnSave.setText("SAVE");
            }
        });

        // Clear form event listener
        binding.btnClear.setOnClickListener(v -> {
            binding.etFirstName.setText("");
            binding.etLastName.setText("");
            binding.etEmail.setText("");
            binding.etPhone.setText("");
            binding.etAddress.setText("");
            binding.radioGroupGender.clearCheck();
            binding.etAddress.setText("");
            binding.etCity.setText("");
            binding.etRegion.setText("");
            binding.ivAvatar.setImageResource(R.drawable.avatar);
            createCustomerViewModel.onClearCustomerErrors();
            customerViewModel.onClearCustomer();
            isEditMode = false;
            createCustomerViewModel.setIsEditingMode(false);
        });

        // Save form event listener
        binding.btnSave.setOnClickListener(v -> {
            if (isEditMode) {
                createCustomerViewModel.onConfirmCustomerUpdate(customerViewModel.getCustomer().getValue(), new OnConfirmCustomerCallback() {
                    @Override
                    public void onSuccessful() {
                        Toast.makeText(getContext(), "Customer updated successfully", Toast.LENGTH_SHORT).show();
                        customerViewModel.onClearCustomer();
                        dismiss();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(getContext(), "Failed to update customer", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                createCustomerViewModel.onConfirmCustomerSave(customerViewModel.getCustomer().getValue(), new OnConfirmCustomerCallback() {
                    @Override
                    public void onSuccessful() {
                        Toast.makeText(getContext(), "Customer created successfully", Toast.LENGTH_SHORT).show();
                        customerViewModel.onClearCustomer();

                        // Find and dismiss the CustomersFragment by tag
                        FragmentManager fragmentManager = getParentFragmentManager();
                        CustomersFragment customerDialogFragment = (CustomersFragment)
                                fragmentManager.findFragmentByTag(CustomersFragment.TAG);

                        if (customerDialogFragment != null) {
                            customerDialogFragment.dismiss();
                        }

                        dismiss();
                    }

                    @Override
                    public void onFailed() {
                        Toast.makeText(getContext(), "Failed to create customer", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        binding.closeButton.setOnClickListener(v -> {
            dismiss();
        });

    }

    @Override
    public void dismiss() {
        super.dismiss();
        customerViewModel.onLoadCurrentCustomer();
    }
}