package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thanh
 * @since 3/5/2015
 */
public class PlaceOrderProductResult implements Parcelable {

    public static final Creator<PlaceOrderProductResult> CREATOR = new Creator<PlaceOrderProductResult>() {
        public PlaceOrderProductResult createFromParcel(Parcel in) {
            return new PlaceOrderProductResult(in);
        }

        public PlaceOrderProductResult[] newArray(int size) {
            return new PlaceOrderProductResult[size];
        }
    };

    @JsonProperty("list")
    private PlaceOrderProduct[] items;
    private int count;

    public PlaceOrderProductResult() {
        super();
    }

    public PlaceOrderProductResult(Parcel in) {
        items = in.createTypedArray(PlaceOrderProduct.CREATOR);
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(items, flags);
        dest.writeInt(count);
    }

    public PlaceOrderProduct[] getItems() {
        return items;
    }

    public void setItems(PlaceOrderProduct[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
