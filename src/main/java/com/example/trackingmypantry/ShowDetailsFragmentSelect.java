package com.example.trackingmypantry;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class ShowDetailsFragmentSelect extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_show_details_select, container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        assert getArguments() != null;
        Product productSelected = getArguments().getParcelable("productSelected");

        TextView showSelectName = view.findViewById(R.id.showSelectName);
        TextView showSelectBarcode = view.findViewById(R.id.showSelectBarcode);
        TextView showSelectDescription = view.findViewById(R.id.showSelectDescription);

        showSelectName.setText(productSelected.name);
        showSelectBarcode.setText(productSelected.barcode);
        showSelectDescription.setText(productSelected.description);

        //if the product is selected pass it to SelectItemActivity
        Button buttonSelect = view.findViewById(R.id.showSelectButton);
        buttonSelect.setOnClickListener(v -> {
            getParentFragmentManager().setFragmentResult("productSelected", new Bundle());
            Toast.makeText(requireContext(), productSelected.name + " aggiunto alla tua dispensa",
                    Toast.LENGTH_SHORT).show();
            dismiss();
        });

    }
}
