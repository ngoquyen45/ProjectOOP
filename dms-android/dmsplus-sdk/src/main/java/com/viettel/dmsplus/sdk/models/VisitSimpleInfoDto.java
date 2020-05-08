package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.text.TextUtils;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class VisitSimpleInfoDto extends IdDto implements Serializable {

    public static final Creator<VisitSimpleInfoDto> CREATOR = new Creator<VisitSimpleInfoDto>() {
        public VisitSimpleInfoDto createFromParcel(Parcel in) {
            return new VisitSimpleInfoDto(in);
        }

        public VisitSimpleInfoDto[] newArray(int size) {
            return new VisitSimpleInfoDto[size];
        }
    };
    
    private boolean hasOrder;
    private boolean closed;
    private String closingPhoto;
    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date startTime;
    @JsonDeserialize(using = IsoDateDeserializer.class)
    private Date endTime;
    private long duration;
    private boolean errorDuration;
    private int locationStatus;
    private Double distance;
    private BigDecimal grandTotal;
    private int approveStatus;

    public VisitSimpleInfoDto() {
        super();
    }
    
    public VisitSimpleInfoDto(Parcel in) {
        super(in);
        
        this.hasOrder = in.readByte() != 0;
        this.closed = in.readByte() != 0;
        this.closingPhoto = in.readString();
        long time = in.readLong();
        this.startTime = time != 0 ? new Date(time) : null;
        time = in.readLong();
        this.endTime = time != 0 ? new Date(time) : null;
        this.duration = in.readLong();
        this.errorDuration = in.readByte() != 0;
        this.locationStatus = in.readInt();
        this.distance = in.readDouble();
        if (this.distance < 0) {
            this.distance = null;
        }
        String number = in.readString();
        this.grandTotal = TextUtils.isEmpty(number) ? null : new BigDecimal(number);
        this.approveStatus = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeByte((byte) (hasOrder ? 1 : 0));
        dest.writeByte((byte) (closed ? 1 : 0));
        dest.writeString(closingPhoto);
        dest.writeLong(startTime != null ? startTime.getTime() : 0);
        dest.writeLong(endTime != null ? endTime.getTime() : 0);
        dest.writeLong(duration);
        dest.writeByte((byte) (errorDuration ? 1 : 0));
        dest.writeInt(locationStatus);
        dest.writeDouble(distance != null ? distance : -1);
        dest.writeString(grandTotal != null ? grandTotal.toString() : "");
        dest.writeInt(approveStatus);
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getClosingPhoto() {
        return closingPhoto;
    }

    public void setClosingPhoto(String closingPhoto) {
        this.closingPhoto = closingPhoto;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isErrorDuration() {
        return errorDuration;
    }

    public void setErrorDuration(boolean errorDuration) {
        this.errorDuration = errorDuration;
    }

    public int getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(int locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }
}
