package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by PHAMHUNG on 2/2/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExchangeReturnSimpleListResult implements Parcelable {

    public static final Parcelable.Creator<ExchangeReturnSimpleListResult> CREATOR = new Parcelable.Creator<ExchangeReturnSimpleListResult>() {
        public ExchangeReturnSimpleListResult createFromParcel(Parcel in) {
            return new ExchangeReturnSimpleListResult(in);
        }

        public ExchangeReturnSimpleListResult[] newArray(int size) {
            return new ExchangeReturnSimpleListResult[size];
        }
    };

    @JsonProperty("list")
    private ExchangeReturnSimpleDto[] items;
    private int count;

    public ExchangeReturnSimpleListResult() {

    }

    public ExchangeReturnSimpleListResult(Parcel in) {
        items = in.createTypedArray(ExchangeReturnSimpleDto.CREATOR);
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

    public ExchangeReturnSimpleDto[] getItems() {
        return items;
    }

    public void setItems(ExchangeReturnSimpleDto[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
