package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer;

import java.util.Date;

/**
 * @author Thanh
 * @since 2/9/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesMonthlySummaryResult implements Parcelable {

    public static final Creator<SalesMonthlySummaryResult> CREATOR = new Creator<SalesMonthlySummaryResult>() {
        public SalesMonthlySummaryResult createFromParcel(Parcel in) {
            return new SalesMonthlySummaryResult(in);
        }

        public SalesMonthlySummaryResult[] newArray(int size) {
            return new SalesMonthlySummaryResult[size];
        }
    };

    @JsonProperty("list")
    private SalesDailySummaryItem[] items;
    private int count;

    public SalesMonthlySummaryResult() {
    }

    public SalesMonthlySummaryResult(Parcel in) {
        items = in.createTypedArray(SalesDailySummaryItem.CREATOR);
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

    public SalesDailySummaryItem[] getItems() {
        return items;
    }

    public void setItems(SalesDailySummaryItem[] items) {
        this.items = items;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SalesDailySummaryItem implements Parcelable, Comparable<SalesDailySummaryItem> {

        public static Creator<SalesDailySummaryItem> CREATOR = new Creator<SalesDailySummaryItem>() {
            public SalesDailySummaryItem createFromParcel(Parcel in) {
                return new SalesDailySummaryItem(in);
            }

            public SalesDailySummaryItem[] newArray(int size) {
                return new SalesDailySummaryItem[size];
            }
        };

        @JsonDeserialize(using = IsoShortDateDeserializer.class)
        private Date date;
        private SalesResult salesResult;

        public SalesDailySummaryItem() {
        }

        public SalesDailySummaryItem(Parcel in) {
            long time = in.readLong();
            date = time != 0 ? new Date(time) : null;
            salesResult = in.readParcelable(SalesResult.class.getClassLoader());
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeLong(date != null ? date.getTime() : 0);
            dest.writeParcelable(salesResult, flags);
        }

        @Override
        public int compareTo(@NonNull SalesDailySummaryItem another) {
            return date.compareTo(another.date);
        }

        public Date getDate() {
            return date;
        }

        public void setDate(Date date) {
            this.date = date;
        }

        public SalesResult getSalesResult() {
            return salesResult;
        }

        public void setSalesResult(SalesResult salesResult) {
            this.salesResult = salesResult;
        }
    }
}
