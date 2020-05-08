package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;

/**
 * @author Thanh
 * @since 9/29/15
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SalesResult implements Parcelable {

    public static final Creator<SalesResult> CREATOR = new Creator<SalesResult>() {
        public SalesResult createFromParcel(Parcel source) {
            return new SalesResult(source);
        }

        public SalesResult[] newArray(int size) {
            return new SalesResult[size];
        }
    };

    private BigDecimal revenue;
    private BigDecimal productivity;
    private int nbOrder;

    public SalesResult() {
    }

    protected SalesResult(Parcel in) {
        this.revenue = (BigDecimal) in.readSerializable();
        this.productivity = (BigDecimal) in.readSerializable();
        this.nbOrder = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeSerializable(this.revenue);
        dest.writeSerializable(this.productivity);
        dest.writeInt(this.nbOrder);
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(BigDecimal revenue) {
        this.revenue = revenue;
    }

    public BigDecimal getProductivity() {
        return productivity;
    }

    public void setProductivity(BigDecimal productivity) {
        this.productivity = productivity;
    }

    public int getNbOrder() {
        return nbOrder;
    }

    public void setNbOrder(int nbOrder) {
        this.nbOrder = nbOrder;
    }

}
