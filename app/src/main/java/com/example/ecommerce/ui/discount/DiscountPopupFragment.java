package com.example.ecommerce.ui.discount;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.ecommerce.App;
import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.dao.DiscountDao;
import com.example.ecommerce.dao.IDiscountDao;
import com.example.ecommerce.databinding.FragmentDiscountPopupBinding;
import com.example.ecommerce.model.Discount;
import com.example.ecommerce.repository.DiscountRepository;
import com.example.ecommerce.repository.IDiscountRepository;
import com.example.ecommerce.utils.DatabaseHelper;

import java.util.Date;

public class DiscountPopupFragment extends DialogFragment {

    private static final String TAG = "DiscountPopupFragment";
    private static DiscountViewModel discountViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_discount_popup, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        FragmentDiscountPopupBinding binding = FragmentDiscountPopupBinding.bind(view);

        binding.closeButton.setOnClickListener(v -> {
            Fragment fragment = getParentFragmentManager().findFragmentByTag("DiscountPopupFragment");
            if (fragment != null) {
                getParentFragmentManager().beginTransaction().remove(fragment).commit();
            }
        });

        discountViewModel = new ViewModelProvider(requireActivity(), App.appModule.provideDiscountViewModelFactory()).get(DiscountViewModel.class);

        binding.oneButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(1, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.twoButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(2, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.threeButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(3, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.fourButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(4, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.fiveButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(5, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.sixButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(6, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.sevenButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(7, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.eightButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(8, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.nineButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(9, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.zeroButton.setOnClickListener(v -> {
            Double currentDiscountValue = discountViewModel.getDiscountValue().getValue();
            String modifiedDiscountValue = modifyDiscountValue(0, currentDiscountValue);
            discountViewModel.setDiscountValue(Double.parseDouble(modifiedDiscountValue));
        });

        binding.clearButton.setOnClickListener(v -> {
            discountViewModel.onClearDiscount();
        });

        binding.discountRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.percentage_off_radio_button) {
                discountViewModel.setDiscountType("percentage");
            } else if (checkedId == R.id.fixed_amount_off_radio_button) {
                discountViewModel.setDiscountType("fixed");
            }
        });

        binding.addButton.setOnClickListener(v -> {
            String discountType = discountViewModel.getDiscountType().getValue();
            Double discountValue = discountViewModel.getDiscountValue().getValue();
            if (discountType == null || discountValue == null || discountValue == 0.0 || discountType.isEmpty()) {
                Toast.makeText(getContext(), "Please select a discount type and value", Toast.LENGTH_SHORT).show();
                return;
            }
            Discount discount = new Discount.DiscountBuilder()
                    .withDiscountType(discountType)
                    .withDiscountValue(discountValue)
                    .build();
            discountViewModel.onAddDiscount(discount);
            Toast.makeText(getContext(), "Discount added", Toast.LENGTH_SHORT).show();
        });

        // observe the discount value
        discountViewModel.getDiscountValue().observe(getViewLifecycleOwner(), discountValue -> {
            binding.discountAmountTextView.setText(String.valueOf(discountValue));
        });

        // observe the discount type
        discountViewModel.getDiscountType().observe(getViewLifecycleOwner(), discountType -> {
            if (discountType.equals("percentage")) {
                binding.percentageOffRadioButton.setChecked(true);
            } else if (discountType.equals("fixed")) {
                binding.fixedAmountOffRadioButton.setChecked(true);
            } else {
                binding.discountRadioGroup.clearCheck();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // set the width of the dialog to match the width of the screen
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }

    public String modifyDiscountValue(int newValue, Double currentDiscountValue) {
        String currentDiscountValueString = (currentDiscountValue != null) ? currentDiscountValue.toString() : "0.0";

        if (currentDiscountValue == 0.0) {
            if (newValue == 0) {
                return currentDiscountValueString; // If both current discount and new value are 0, return "0.0"
            }
        }

        // split string from decimal point, and add new value to the end of the string
        String[] parts = currentDiscountValueString.split("\\.");
        if (parts.length == 1) {
            return currentDiscountValueString + newValue;
        } else {
            return parts[0] + newValue + "." + parts[1];
        }
    }
}