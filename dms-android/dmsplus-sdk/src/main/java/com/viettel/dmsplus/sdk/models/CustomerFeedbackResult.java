package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author PHAMHUNG
 * @since 4/2/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerFeedbackResult implements Parcelable {

    private CustomerFeedbackModel[] list;
    private int count;

    public CustomerFeedbackModel[] getList() {
        return list;
    }

    public void setList(CustomerFeedbackModel[] list) {
        this.list = list;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelableArray(this.list, 0);
        dest.writeInt(this.count);
    }

    public CustomerFeedbackResult() {
    }

    protected CustomerFeedbackResult(Parcel in) {
        this.list = (CustomerFeedbackModel[]) in.readParcelableArray(CustomerFeedbackModel.class.getClassLoader());
        this.count = in.readInt();
    }

    public static final Creator<CustomerFeedbackResult> CREATOR = new Creator<CustomerFeedbackResult>() {
        public CustomerFeedbackResult createFromParcel(Parcel source) {
            return new CustomerFeedbackResult(source);
        }

        public CustomerFeedbackResult[] newArray(int size) {
            return new CustomerFeedbackResult[size];
        }
    };
}
