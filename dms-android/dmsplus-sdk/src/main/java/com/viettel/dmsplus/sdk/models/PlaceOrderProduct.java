package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Thanh
 * @since 3/5/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlaceOrderProduct extends Product implements Comparable<PlaceOrderProduct>, Serializable {

    public static final Creator<PlaceOrderProduct> CREATOR = new Creator<PlaceOrderProduct>() {
        public PlaceOrderProduct createFromParcel(Parcel in) {
            return new PlaceOrderProduct(in);
        }

        public PlaceOrderProduct[] newArray(int size) {
            return new PlaceOrderProduct[size];
        }
    };

    @JsonIgnore
    private int quantity;
    private int seqNo;

    public PlaceOrderProduct() {

    }

    public PlaceOrderProduct(Parcel in) {
        super(in);

        quantity = in.readInt();
        seqNo = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(quantity);
        dest.writeInt(seqNo);
    }

    @Override
    public int compareTo(@NonNull PlaceOrderProduct another) {
        return seqNo - another.seqNo;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

}
