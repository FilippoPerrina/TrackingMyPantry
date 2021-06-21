package com.example.trackingmypantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ShowDetailsFragmentMain extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_details_main, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView showName = view.findViewById(R.id.showName);
        TextView showBarcode = view.findViewById(R.id.showBarcode);
        TextView showDescription = view.findViewById(R.id.showDescription);
        TextView showCategory = view.findViewById(R.id.showCategory);
        TextView showQuantity = view.findViewById(R.id.showQuantity);
        TextView showEndDate = view.findViewById(R.id.showEndDate);

        assert getArguments() != null;
        Product productDetails = getArguments().getParcelable("productDetails");
        showName.setText(productDetails.name);
        showBarcode.setText(productDetails.barcode);
        showDescription.setText(productDetails.description);

        //if the property is not setted hide the textView
        String categoryValue = getArguments().getString("category");
        if ( categoryValue == null ) {
            showCategory.setVisibility(View.INVISIBLE);
        } else {
            showCategory.setText(categoryValue);
        }
        String quantityValue = getArguments().getString("quantity");
        if ( quantityValue == null ) {
            showQuantity.setVisibility(View.INVISIBLE);
        } else {
            showQuantity.setText(quantityValue);
        }
        String endDateValue = getArguments().getString("endDate");
        if ( endDateValue == null ) {
            showEndDate.setVisibility(View.INVISIBLE);
        } else {
            showEndDate.setText(endDateValue);
        }

        String[] values = getResources().getStringArray(R.array.select_category);

        Button buttonAddCategory = view.findViewById(R.id.buttonAddCategory);
        buttonAddCategory.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
            builder.setTitle("Scegli la categoria")
                    .setItems(R.array.select_category, (dialog, which) -> {
                            Bundle bundle = new Bundle();
                            bundle.putString("categorySelected", values[which]);
                            getParentFragmentManager().setFragmentResult("requestKeyCategory", bundle);
                            showCategory.setText(values[which]);
                            showCategory.setVisibility(View.VISIBLE);
                    });
            AlertDialog alert = builder.create();
            alert.show();
        });


        //open a quantityPicker in an alertDialog
        Button buttonAddQuantity = view.findViewById(R.id.buttonAddQuantity);
        buttonAddQuantity.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
            alertDialogBuilder.setTitle("Imposta la quantità");
            LayoutInflater inflater = this.getLayoutInflater();
            View viewQuantity = inflater.inflate(R.layout.dialog_quantity, null);
            alertDialogBuilder.setView(viewQuantity);
            NumberPicker quantityPicker = viewQuantity.findViewById(R.id.numberPickerQuantity);
            quantityPicker.setMinValue(1);
            quantityPicker.setMaxValue(100);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (dialog, id) -> {
                                Bundle bundle = new Bundle();
                                bundle.putString("quantitySelected", String.valueOf(quantityPicker.getValue()));
                                getParentFragmentManager().setFragmentResult("requestKeyQuantity", bundle);
                                showQuantity.setText(String.valueOf(quantityPicker.getValue()));
                                showQuantity.setVisibility(View.VISIBLE);
                            })
                    .setNegativeButton("Annulla", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

        //open a datepicker in an alertDialog
        Button buttonAddEndDate = view.findViewById(R.id.buttonAddEndDate);
        buttonAddEndDate.setOnClickListener(v -> {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireContext());
            alertDialogBuilder.setTitle("Imposta la data di scadenza");
            LayoutInflater inflater = this.getLayoutInflater();
            View viewEndDate = inflater.inflate(R.layout.dialog_end_date, null);
            alertDialogBuilder.setView(viewEndDate);
            DatePicker datePicker = viewEndDate.findViewById(R.id.endDatePicker);
            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Ok",
                            (dialog, id) -> {
                                Bundle bundle = new Bundle();
                                String endDateSelected = datePicker.getYear() + "-" +
                                        datePicker.getMonth() + "-" + datePicker.getDayOfMonth();
                                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                                try {
                                    Date date = format.parse(endDateSelected);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(date);
                                    cal.add(Calendar.MONTH,1);
                                    Date dateOneMonth = cal.getTime();
                                    endDateSelected = format.format(dateOneMonth);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                bundle.putString("endDateSelected", endDateSelected);
                                getParentFragmentManager().setFragmentResult("requestKeyDate", bundle);
                                showEndDate.setText(endDateSelected);
                                showEndDate.setVisibility(View.VISIBLE);
                            })
                    .setNegativeButton("Annulla", (dialog, id) -> dialog.cancel());
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        });

    }
}
