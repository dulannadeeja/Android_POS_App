package com.example.ecommerce.features.summary;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.ecommerce.MainActivity;
import com.example.ecommerce.R;
import com.example.ecommerce.databinding.FragmentSummaryBinding;
import com.example.ecommerce.features.products.ProductsFragment;
import com.google.android.material.appbar.MaterialToolbar;

public class SummaryFragment extends DialogFragment {
    private static final String TAG = "SummaryFragment";
    private static final String ARG_TOTAL_PAID_AMOUNT = "totalPaidAmount";
    private static final String ARG_CHANGE_AMOUNT = "changeAmount";
    private double totalPaidAmount;
    private double changeAmount;

    public static SummaryFragment newInstance(double totalPaidAmount, double changeAmount) {
        SummaryFragment fragment = new SummaryFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_TOTAL_PAID_AMOUNT, totalPaidAmount);
        args.putDouble(ARG_CHANGE_AMOUNT, changeAmount);
        fragment.setArguments(args);
        return fragment;
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_summary, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // get the arguments
        if (getArguments() != null) {
            totalPaidAmount = getArguments().getDouble(ARG_TOTAL_PAID_AMOUNT);
            changeAmount = getArguments().getDouble(ARG_CHANGE_AMOUNT);
        }

        FragmentSummaryBinding binding = FragmentSummaryBinding.bind(view);

        MaterialToolbar toolbar = view.findViewById(R.id.summary_toolbar);
        toolbar.inflateMenu(R.menu.menu_summary_appbar);
        toolbar.setTitle("Summary");
        toolbar.setTitleTextColor(getResources().getColor(R.color.backgroundColor));
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.share) {
                Toast.makeText(getContext(), "Share clicked", Toast.LENGTH_SHORT).show();
                return true;
            } else {
                return false;
            }
        });

        binding.tvChangeAmount.setText(String.valueOf(changeAmount));
        binding.tvTotalPaidAmount.setText(String.valueOf(totalPaidAmount));

        binding.newOrderButton.setOnClickListener(v -> {
            dismiss();
            ((MainActivity) requireActivity()).loadFragment(new ProductsFragment(),false);
        });
    }
}