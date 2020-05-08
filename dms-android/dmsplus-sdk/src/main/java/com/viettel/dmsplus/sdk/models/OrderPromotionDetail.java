package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * @author Thanh
 * @since 05/25/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OrderPromotionDetail extends CategorySimple implements Serializable {

    public static final Creator<OrderPromotionDetail> CREATOR = new Creator<OrderPromotionDetail>() {
        public OrderPromotionDetail createFromParcel(Parcel in) {
            return new OrderPromotionDetail(in);
        }

        public OrderPromotionDetail[] newArray(int size) {
            return new OrderPromotionDetail[size];
        }
    };

    private String conditionProductName;
    private OrderPromotionReward reward;

    public OrderPromotionDetail() {

    }

    public OrderPromotionDetail(Parcel in) {
        super(in);

        conditionProductName = in.readString();
        reward = in.readParcelable(this.getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(conditionProductName);
        dest.writeParcelable(reward, flags);
    }

    public OrderPromotionReward getReward() {
        return reward;
    }

    public void setReward(OrderPromotionReward reward) {
        this.reward = reward;
    }

    public String getConditionProductName() {
        return conditionProductName;
    }

    public void setConditionProductName(String conditionProductName) {
        this.conditionProductName = conditionProductName;
    }

}
