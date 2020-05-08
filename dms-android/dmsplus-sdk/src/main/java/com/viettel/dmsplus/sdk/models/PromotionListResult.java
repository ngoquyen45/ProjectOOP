package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author Thanh
 * @since 3/10/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class PromotionListResult implements Parcelable {

    public static final Creator<PromotionListResult> CREATOR = new Creator<PromotionListResult>() {
        public PromotionListResult createFromParcel(Parcel in) {
            return new PromotionListResult(in);
        }

        public PromotionListResult[] newArray(int size) {
            return new PromotionListResult[size];
        }
    };

    private PromotionListItem[] list;
    private int count;

    public PromotionListResult() {

    }

    public PromotionListResult(Parcel in) {
        list = in.createTypedArray(PromotionListItem.CREATOR);
        count = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedArray(list, flags);
        dest.writeInt(count);
    }

    public PromotionListItem[] getList() {
        return list;
    }

    public void setList(PromotionListItem[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
