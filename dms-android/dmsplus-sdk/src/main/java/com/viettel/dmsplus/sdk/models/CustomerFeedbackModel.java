package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.io.Serializable;
import java.util.Date;

/**
 * @author PHAMHUNG
 * @since 3/24/2015
 */
public class CustomerFeedbackModel implements Parcelable, Comparable<CustomerFeedbackModel>, Serializable {

    public static final Creator<CustomerFeedbackModel> CREATOR = new Creator<CustomerFeedbackModel>() {
        public CustomerFeedbackModel createFromParcel(Parcel in) {
            return new CustomerFeedbackModel(in);
        }

        public CustomerFeedbackModel[] newArray(int size) {
            return new CustomerFeedbackModel[size];
        }
    };

    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date createdTime;
    private String message;

    @JsonIgnore
    private boolean isLastCommentOfDate;
    @JsonIgnore
    private boolean isFirstCommentOfDate;
    @JsonIgnore
    private boolean position;

    public CustomerFeedbackModel() {
    }

    public CustomerFeedbackModel(Parcel in) {
        setCreatedTime(new Date(in.readLong()));
        setMessage(in.readString());
        position = in.readByte() != 0;
        isLastCommentOfDate = in.readByte() != 0;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(getCreatedTime().getTime());
        parcel.writeString(getMessage());
        parcel.writeByte((byte) (isPosition() ? 1 : 0));
        parcel.writeByte((byte) (isLastCommentOfDate() ? 1 : 0));
    }

    @Override
    public int compareTo(@NonNull CustomerFeedbackModel another) {
        return getCreatedTime().compareTo(another.getCreatedTime());
    }

    public boolean isLastCommentOfDate() {
        return isLastCommentOfDate;
    }

    public void setLastCommentOfDate(boolean isHaveTextDate) {
        this.isLastCommentOfDate = isHaveTextDate;
    }

    public boolean isPosition() {
        return position;
    }

    public void setPosition(boolean position) {
        this.position = position;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFirstCommentOfDate() {
        return isFirstCommentOfDate;
    }

    public void setFirstCommentOfDate(boolean isFirstCommentOfDate) {
        this.isFirstCommentOfDate = isFirstCommentOfDate;
    }

}
