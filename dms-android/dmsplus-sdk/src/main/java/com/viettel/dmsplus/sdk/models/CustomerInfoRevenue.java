package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * @author PHAMHUNG
 * @since 3/2/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerInfoRevenue implements Parcelable {

    public static final Creator<CustomerInfoRevenue> CREATOR = new Creator<CustomerInfoRevenue>() {
        public CustomerInfoRevenue createFromParcel(Parcel in) {
            return new CustomerInfoRevenue(in);
        }

        public CustomerInfoRevenue[] newArray(int size) {
            return new CustomerInfoRevenue[size];
        }
    };

    @JsonProperty("month")
    private String month;
    @JsonProperty("revenue")
    private BigDecimal revenue;

    public CustomerInfoRevenue() {

    }

    public CustomerInfoRevenue(Parcel in) {
        this.month = in.readString();
        setRevenue(new BigDecimal(in.readString()));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getMonth());
        dest.writeString(getRevenue().toString());
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

}

