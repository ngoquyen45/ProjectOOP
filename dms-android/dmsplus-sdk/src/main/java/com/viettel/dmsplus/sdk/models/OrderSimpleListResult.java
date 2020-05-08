package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Thanh
 * @since 3/24/2015
 */
public class OrderSimpleListResult implements Parcelable {

    public static final Creator<OrderSimpleListResult> CREATOR = new Creator<OrderSimpleListResult>() {
        public OrderSimpleListResult createFromParcel(Parcel in) {
            return new OrderSimpleListResult(in);
        }

        public OrderSimpleListResult[] newArray(int size) {
            return new OrderSimpleListResult[size];
        }
    };

    @JsonProperty("list")
    private OrderSimpleResult[] items;
    private int count;

    public OrderSimpleListResult() {

    }

    public OrderSimpleListResult(Parcel in) {
        items = in.createTypedArray(OrderSimpleResult.CREATOR);
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int i) {
        dest.writeTypedArray(items, i);
        dest.writeInt(count);
    }

    public OrderSimpleResult[] getItems() {
        return items;
    }

    public void setItems(OrderSimpleResult[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
