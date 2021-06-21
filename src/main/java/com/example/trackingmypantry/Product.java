package com.example.trackingmypantry;

import android.os.Parcel;
import android.os.Parcelable;

//useful class to wrap the properties received by the server
public class Product implements Parcelable {

    public String id;
    public String name;
    public String description;
    public String barcode;

    protected Product(String id, String name, String description, String barcode) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.barcode = barcode;
    }

    public Product(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.description = in.readString();
        this.barcode = in.readString();
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel in) {
            return new Product(in);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(barcode);
    }
}
