package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Thanh
 * @since 3/9/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPromotion extends CategorySimple implements Serializable {

    public static final Creator<OrderPromotion> CREATOR = new Creator<OrderPromotion>() {

        public OrderPromotion createFromParcel(Parcel in) {
            return new OrderPromotion(in);
        }

        public OrderPromotion[] newArray(int size) {
            return new OrderPromotion[size];
        }
    };

    private OrderPromotionDetail[] details;

    public OrderPromotion() {

    }

    public OrderPromotion(Parcel in) {
        super(in);
        details = in.createTypedArray(OrderPromotionDetail.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeTypedArray(details, flags);
    }

    public OrderPromotionDetail[] getDetails() {
        return details;
    }

    public void setDetails(OrderPromotionDetail[] details) {
        this.details = details;
    }

}
