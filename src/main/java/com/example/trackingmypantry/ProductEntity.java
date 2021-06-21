package com.example.trackingmypantry;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "product_table")
public class ProductEntity {

    //using the productName as a PrimaryKey because duplicate are not allowed
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "product")
    private final String product;

    @ColumnInfo(name = "description")
    private final String description;

    @ColumnInfo(name = "id")
    private final String id;

    @ColumnInfo(name = "barcode")
    private final String barcode;

    @ColumnInfo(name = "quantity")
    private String quantity;

    @ColumnInfo(name = "endDate")
    private String endDate;

    @ColumnInfo(name = "category")
    private String category;

    public ProductEntity(String id, @NonNull String product, String description, String barcode) {
        this.product = product;
        this.description = description;
        this.id = id;
        this.barcode = barcode;
    }

    public String getId() { return this.id; }
    @NonNull
    public String getProduct() { return this.product; }
    public String getDescription() { return this.description; }
    public String getBarcode() { return this.barcode; }
    public String getQuantity() { return this.quantity; }
    public String getEndDate() { return this.endDate; }
    public String getCategory() { return this.category; }
    public void setQuantity(String quantity) { this.quantity = quantity; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setCategory(String category) { this.category = category; }
}
