package com.example.trackingmypantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

//adapter of the list showed in SelectItemActivity
public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.ViewHolder> {
    private final ArrayList<String> values;
    private final OnItemClick mCallback;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView productName;
        public Button button;

        public ViewHolder(View v) {
            super(v);
            productName = (TextView)v.findViewById(R.id.textViewName);
            button = (Button)v.findViewById(R.id.buttonSelectItem);
        }
    }

    public SelectAdapter(ArrayList<String> values, OnItemClick listener) {
        this.values = values;
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.recycler_product, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.productName.setText(values.get(position));
        holder.button.setOnClickListener(v-> mCallback.onClick(position));
    }

    @Override
    public int getItemCount() {
        return values.size();
    }


    public interface OnItemClick {
        void onClick (int position);
    }

}
