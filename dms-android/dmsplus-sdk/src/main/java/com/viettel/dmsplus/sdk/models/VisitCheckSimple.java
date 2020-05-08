package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.viettel.dmsplus.sdk.network.IsoDateDeserializer;

import java.util.Date;

/**
 * @author thanh
 * @since 10/13/15
 */
public class VisitCheckSimple extends OrderSimpleResult {

    public static final int CHECK_STATUS_FAIL = 0;
    public static final int CHECK_STATUS_PASS = 1;
    public static final int CHECK_STATUS_OTHER = 2;

    public static final Creator<VisitCheckSimple> CREATOR = new Creator<VisitCheckSimple>() {
        public VisitCheckSimple createFromParcel(Parcel source) {
            return new VisitCheckSimple(source);
        }

        public VisitCheckSimple[] newArray(int size) {
            return new VisitCheckSimple[size];
        }
    };

    private boolean hasOrder;

    private int visitStatus;
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

    private boolean checked;

    private UserSimple salesman;

    public VisitCheckSimple() {
        super();
    }

    public VisitCheckSimple(VisitCheckSimple other) {
        super(other);
        this.hasOrder = other.hasOrder;

        this.visitStatus = other.visitStatus;
        this.closed = other.closed;
        this.closingPhoto = other.closingPhoto;

        this.startTime = other.startTime;
        this.endTime = other.endTime;

        this.duration = other.duration;
        this.errorDuration = other.errorDuration;

        this.locationStatus = other.locationStatus;
        this.distance = other.distance;

        this.checked = other.checked;

        this.salesman = other.salesman;
    }

    public VisitCheckSimple(Parcel in) {
        super(in);

        this.hasOrder = in.readByte() != 0;

        this.visitStatus = in.readInt();
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

        this.checked = in.readByte() != 0;

        this.salesman = in.readParcelable(UserSimple.class.getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeByte((byte) (hasOrder ? 1 : 0));

        dest.writeInt(visitStatus);
        dest.writeByte((byte) (closed ? 1 : 0));
        dest.writeString(closingPhoto);

        dest.writeLong(startTime != null ? startTime.getTime() : 0);
        dest.writeLong(endTime != null ? endTime.getTime() : 0);

        dest.writeLong(duration);
        dest.writeByte((byte) (errorDuration ? 1 : 0));

        dest.writeInt(locationStatus);
        dest.writeDouble(distance != null ? distance : -1);

        dest.writeByte((byte) (checked ? 1 : 0));

        dest.writeParcelable(salesman, flags);
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

    public int getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(int visitStatus) {
        this.visitStatus = visitStatus;
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

    public UserSimple getSalesman() {
        return salesman;
    }

    public void setSalesman(UserSimple salesman) {
        this.salesman = salesman;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
