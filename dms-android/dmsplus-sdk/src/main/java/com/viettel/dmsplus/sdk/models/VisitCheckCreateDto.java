package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.viettel.dmsplus.sdk.network.IsoShortDateSerializer;

import java.util.Date;

public class VisitCheckCreateDto extends IdDto {

    public static final Creator<VisitCheckCreateDto> CREATOR = new Creator<VisitCheckCreateDto>() {
        public VisitCheckCreateDto createFromParcel(Parcel in) {
            return new VisitCheckCreateDto(in);
        }

        public VisitCheckCreateDto[] newArray(int size) {
            return new VisitCheckCreateDto[size];
        }
    };

    private int checkStatus;
    private String note;
    private String deliveryManId;
    @JsonSerialize(using = IsoShortDateSerializer.class)
    private Date deliveryDate;

    public VisitCheckCreateDto() {
        super();
    }

    public VisitCheckCreateDto(Parcel in) {
        super(in);

        this.checkStatus = in.readInt();
        this.note = in.readString();
        this.deliveryManId = in.readString();
        long time = in.readLong();
        this.deliveryDate = time > 0 ? new Date(time) : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeInt(checkStatus);
        dest.writeString(note);
        dest.writeString(deliveryManId);
        dest.writeLong(deliveryDate == null ? 0 : deliveryDate.getTime());
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

    public String getDeliveryManId() {
        return deliveryManId;
    }
    
    public void setDeliveryManId(String deliveryManId) {
        this.deliveryManId = deliveryManId;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

}
