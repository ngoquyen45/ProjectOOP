package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Thanh on 3/9/2015.
 */
public class ProductAndQuantity implements Parcelable {

    public static final Creator<ProductAndQuantity> CREATOR = new Creator<ProductAndQuantity>() {
        public ProductAndQuantity createFromParcel(Parcel in) {
            return new ProductAndQuantity(in);
        }

        public ProductAndQuantity[] newArray(int size) {
            return new ProductAndQuantity[size];
        }
    };

    @JsonProperty("productId")
    private String id;
    private int quantity;

    public ProductAndQuantity() {

    }

    public ProductAndQuantity(String id, int quantity) {
        this.id = id;
        this.quantity = quantity;
    }

    public ProductAndQuantity(Parcel in) {
        id = in.readString();
        quantity = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeInt(quantity);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
