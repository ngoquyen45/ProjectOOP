package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateDeserializer;

import java.util.Date;

/**
 * @author thanh
 * @since 10/13/15
 */
public class VisitCheckDetail extends VisitCheckSimple {

    public static final Creator<VisitCheckSimple> CREATOR = new Creator<VisitCheckSimple>() {
        public VisitCheckSimple createFromParcel(Parcel source) {
            return new VisitCheckSimple(source);
        }

        public VisitCheckSimple[] newArray(int size) {
            return new VisitCheckSimple[size];
        }
    };

    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    private Date checkDate;
    private int checkStatus;
    private String note;
    @JsonDeserialize(using = IsoShortDateDeserializer.class)
    private Date deliveryDate;

    private UserSimple storeChecker;
    private CategorySimple deliveryMan;

    public VisitCheckDetail() {
        super();
    }

    public VisitCheckDetail(VisitCheckSimple visitCheckSimple) {
        super(visitCheckSimple);
    }

    public VisitCheckDetail(Parcel in) {
        super(in);

        long time = in.readLong();
        this.checkDate = time != 0 ? new Date(time) : null;
        this.checkStatus = in.readInt();
        this.note = in.readString();

        time = in.readLong();
        this.deliveryDate = time != 0 ? new Date(time) : null;

        this.storeChecker = in.readParcelable(UserSimple.class.getClassLoader());
        this.deliveryMan = in.readParcelable(CategorySimple.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeLong(checkDate != null ? checkDate.getTime() : 0);
        dest.writeInt(checkStatus);
        dest.writeString(note);
        dest.writeLong(deliveryDate != null ? deliveryDate.getTime() : 0);

        dest.writeParcelable(storeChecker, flags);
        dest.writeParcelable(deliveryMan, flags);
    }

    public Date getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    public UserSimple getStoreChecker() {
        return storeChecker;
    }

    public void setStoreChecker(UserSimple storeChecker) {
        this.storeChecker = storeChecker;
    }

    public int getCheckStatus() {
        return checkStatus;
    }

    public void setCheckStatus(int checkStatus) {
        this.checkStatus = checkStatus;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public CategorySimple getDeliveryMan() {
        return deliveryMan;
    }

    public void setDeliveryMan(CategorySimple deliveryMan) {
        this.deliveryMan = deliveryMan;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
}
