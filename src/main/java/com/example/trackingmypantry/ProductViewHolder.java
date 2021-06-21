package com.example.trackingmypantry;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ProductViewHolder extends RecyclerView.ViewHolder {

        private final TextView productItemView;
        public Button buttonShowDetails;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            productItemView = (TextView)itemView.findViewById(R.id.textViewName);
            buttonShowDetails = (Button)itemView.findViewById(R.id.buttonSelectItem);
        }

        public void bind(String text){
            productItemView.setText(text);
        }

        static ProductViewHolder create(ViewGroup parent) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.recycler_product, parent, false);
            return new ProductViewHolder(view);
        }



}
