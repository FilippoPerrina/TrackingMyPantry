package com.example.trackingmypantry;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ProductAdapter extends ListAdapter<ProductEntity, ProductViewHolder>  {
    private final OnItemClick mCallback;
    private List<ProductEntity> mList;

    //mList is the updated list to perform filtering
    public void setmList(List<ProductEntity> mList) {
        this.mList = mList;
    }

    protected ProductAdapter(@NonNull DiffUtil.ItemCallback<ProductEntity> diffCallback, OnItemClick listener) {
        super(diffCallback);
        this.mCallback = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return ProductViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        ProductEntity current = getItem(position);
        holder.bind(current.getProduct());
        holder.buttonShowDetails.setOnClickListener(v -> mCallback.onClick(current));
    }

    static class ProductsDiff extends DiffUtil.ItemCallback<ProductEntity> {
        @Override
        public boolean areItemsTheSame(@NonNull ProductEntity oldItem, @NonNull ProductEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ProductEntity oldItem, @NonNull ProductEntity newItem) {
            return oldItem.getProduct().equals(newItem.getProduct());
        }
    }

    @NonNull
    @Override
    public List<ProductEntity> getCurrentList() {
        return super.getCurrentList();
    }

    public interface OnItemClick {
        void onClick (ProductEntity position);
    }

    //create and populated the list with the filtered element
    public List<ProductEntity> filterSearch(String filter) {
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        if (filter.matches("")) {
            listFiltered = listFull;
        } else {
            for (int i = 0; i < listFull.size(); i++) {
                String nameProduct = listFull.get(i).getProduct().toLowerCase().trim();
                if (nameProduct != null) {
                    if (nameProduct.contains(filter.toLowerCase().trim())) {
                        listFiltered.add(listFull.get(i));
                    }
                }
            }
        }
        return listFiltered;
    }

    //all these function performs filtering, check if the properties are not already set by the user,
    //perform casting from string to Date and int when necessary
    public List<ProductEntity> filterCategory(String category) {
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String categoryProduct = listFull.get(i).getCategory();
            if (categoryProduct != null) {
                if (categoryProduct.equals(category)) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterEndDate(String todayString, String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDateFilter = sdf.parse(endDate);
        Date todayDate = sdf.parse(todayString);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String endDateProduct = listFull.get(i).getEndDate();
            if(endDateProduct != null) {
                Date productEndDate = sdf.parse(endDateProduct);
                if ((productEndDate.after(todayDate) || productEndDate.equals(todayDate)) &&
                        (productEndDate.equals(endDateFilter) || productEndDate.before(endDateFilter))) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterQuantity(String quantityTextFrom, String quantityTextTo) {
        int quantityFrom = Integer.parseInt(quantityTextFrom);
        int quantityTo = Integer.parseInt(quantityTextTo);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String quantityProduct = listFull.get(i).getQuantity();
            if ( quantityProduct != null) {
                int productQuantity = Integer.parseInt(quantityProduct);
                if (productQuantity <= quantityTo && productQuantity >= quantityFrom) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterCategoryQuantity(String category, String quantityTextFrom,
                                                      String quantityTextTo) {
        int quantityFrom = Integer.parseInt(quantityTextFrom);
        int quantityTo = Integer.parseInt(quantityTextTo);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String quantityProduct = listFull.get(i).getQuantity();
            String productCategory = listFull.get(i).getCategory();
            if (productCategory != null && quantityProduct != null) {
                int productQuantity = Integer.parseInt(quantityProduct);
                if (productCategory.equals(category) && productQuantity <= quantityTo
                        && productQuantity >= quantityFrom) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterCategoryEndDate(String category, String todayString,
                                                     String endDate) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDateFilter = sdf.parse(endDate);
        Date todayDate = sdf.parse(todayString);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String productEndDate = listFull.get(i).getEndDate();
            String productCategory = listFull.get(i).getCategory();
            if (productEndDate != null && productCategory != null ) {
                Date endDateProduct = sdf.parse(productEndDate);
                if (productCategory.equals(category) && (endDateProduct.after(todayDate) ||
                        endDateProduct.equals(todayDate)) && (endDateProduct.equals(endDateFilter)
                                || endDateProduct.before(endDateFilter))) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterEndDateQuantity(String todayString, String endDate, String quantityTextFrom, String quantityTextTo) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDateFilter = sdf.parse(endDate);
        Date todayDate = sdf.parse(todayString);
        int quantityFrom = Integer.parseInt(quantityTextFrom);
        int quantityTo = Integer.parseInt(quantityTextTo);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String quantityProduct = listFull.get(i).getQuantity();
            String productEndDate = listFull.get(i).getEndDate();
            if (quantityProduct != null && productEndDate != null) {
                Date endDateProduct = sdf.parse(productEndDate);
                int productQuantity = Integer.parseInt(quantityProduct);
                if (productQuantity <= quantityTo && productQuantity >= quantityFrom &&
                        (endDateProduct.after(todayDate) || endDateProduct.equals(todayDate)) &&
                        (endDateProduct.equals(endDateFilter) || endDateProduct.before(endDateFilter))) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

    public List<ProductEntity> filterCategoryEndDateQuantity(String category, String todayString, String endDate, String quantityTextFrom,
                                                             String quantityTextTo) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date endDateFilter = sdf.parse(endDate);
        Date todayDate = sdf.parse(todayString);
        int quantityFrom = Integer.parseInt(quantityTextFrom);
        int quantityTo = Integer.parseInt(quantityTextTo);
        List<ProductEntity> listFull = new ArrayList<>(mList);
        List<ProductEntity> listFiltered = new ArrayList<>();
        for (int i = 0; i < listFull.size(); i++) {
            String productEndDate = listFull.get(i).getEndDate();
            String productCategory = listFull.get(i).getCategory();
            String quantityProduct = listFull.get(i).getQuantity();
            if (productCategory != null && productEndDate != null && quantityProduct != null) {
                Date endDateProduct = sdf.parse(productEndDate);
                int productQuantity = Integer.parseInt(quantityProduct);
                if (productCategory.equals(category) && productQuantity <= quantityTo &&
                        productQuantity >= quantityFrom && (endDateProduct.after(todayDate) ||
                        endDateProduct.equals(todayDate)) && (endDateProduct.equals(endDateFilter)
                        || endDateProduct.before(endDateFilter))) {
                    listFiltered.add(listFull.get(i));
                }
            }
        }
        return listFiltered;
    }

}
