package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Thanh
 * @since 3/9/2015
 */
public class CalculatePromotionRequest implements Parcelable {

    public static final Creator<CalculatePromotionRequest> CREATOR = new Creator<CalculatePromotionRequest>() {
        public CalculatePromotionRequest createFromParcel(Parcel in) {
            return new CalculatePromotionRequest(in);
        }

        public CalculatePromotionRequest[] newArray(int size) {
            return new CalculatePromotionRequest[size];
        }
    };

    private String customerId;
    private ProductAndQuantity[] details;

    public CalculatePromotionRequest() {

    }

    public CalculatePromotionRequest(Parcel in) {
        customerId = in.readString();
        details = in.createTypedArray(ProductAndQuantity.CREATOR);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(customerId);
        dest.writeTypedArray(details, flags);
    }

    public ProductAndQuantity[] getDetails() {
        return details;
    }

    public void setDetails(ProductAndQuantity[] details) {
        this.details = details;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
