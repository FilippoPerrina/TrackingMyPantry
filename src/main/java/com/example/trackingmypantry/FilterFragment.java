package com.example.trackingmypantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class FilterFragment extends DialogFragment {
    private String selectedCategory;
    private String selectedEndDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RadioGroup radioGroupCategory = view.findViewById(R.id.radioGroupCategory);
        radioGroupCategory.setOnCheckedChangeListener((group, checkedId) -> {
            RadioButton checkedRadioButton = view.findViewById(checkedId);
            selectedCategory = checkedRadioButton.getText().toString();
        });

        RadioGroup radioGroupEndDate = view.findViewById(R.id.radioGroupEndDate);
        radioGroupEndDate.setOnCheckedChangeListener(((group, checkedId) -> {
            RadioButton checkedRadioButton = view.findViewById(checkedId);
            selectedEndDate = checkedRadioButton.getText().toString();
        }));

        EditText quantityFrom = view.findViewById(R.id.editTextFromQuantity);
        EditText quantityTo = view.findViewById(R.id.editTextToQuantity);

        //pass the filters selected to MainActivity
        Button buttonApplyFilter = view.findViewById(R.id.buttonApplyFilter);
        buttonApplyFilter.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("selectedCategory", selectedCategory);
            bundle.putString("selectedEndDate", selectedEndDate);
            String quantityTextFrom = quantityFrom.getText().toString();
            String quantityTextTo = quantityTo.getText().toString();
            if ( !quantityTextFrom.matches("") && quantityTextTo.matches("") ||
                    quantityTextFrom.matches("") && !quantityTextTo.matches("") ) {
                Toast.makeText(requireContext(), "Inserisci entrambi le quantità perfavore",
                        Toast.LENGTH_SHORT).show();
            } else {
                bundle.putString("quantityTextFrom", quantityTextFrom);
                bundle.putString("quantityTextTo", quantityTextTo);
                getParentFragmentManager().setFragmentResult("applyFilter", bundle);
                dismiss();
            }

        });

        Button buttonRemoveFilter = view.findViewById(R.id.buttonRemoveFilter);
        buttonRemoveFilter.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            getParentFragmentManager().setFragmentResult("removeFilter", bundle);
            dismiss();
        });
    }
}
