package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * Created on 2/27/2015.
 * @author Thanh
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardInfoItem implements Parcelable {

    public static final Creator<DashboardInfoItem> CREATOR = new Creator<DashboardInfoItem>() {
        public DashboardInfoItem createFromParcel(Parcel source) {
            return new DashboardInfoItem(source);
        }

        public DashboardInfoItem[] newArray(int size) {
            return new DashboardInfoItem[size];
        }
    };

    private BigDecimal plan;
    private BigDecimal actual;
    private BigDecimal remaining;
    private BigDecimal percentage;

    public DashboardInfoItem() {

    }

    protected DashboardInfoItem(Parcel in) {
        this.plan = (BigDecimal) in.readSerializable();
        this.actual = (BigDecimal) in.readSerializable();
        this.remaining = (BigDecimal) in.readSerializable();
        this.percentage = (BigDecimal) in.readSerializable();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.plan);
        dest.writeSerializable(this.actual);
        dest.writeSerializable(this.remaining);
        dest.writeSerializable(this.percentage);
    }

    public BigDecimal getPlan() {
        return plan;
    }

    public void setPlan(BigDecimal plan) {
        this.plan = plan;
    }

    public BigDecimal getActual() {
        return actual;
    }

    public void setActual(BigDecimal actual) {
        this.actual = actual;
    }

    public BigDecimal getRemaining() {
        return remaining;
    }

    public void setRemaining(BigDecimal remaining) {
        this.remaining = remaining;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }

}
