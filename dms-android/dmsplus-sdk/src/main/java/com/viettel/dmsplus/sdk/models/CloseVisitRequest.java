package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by Thanh on 3/10/2015.
 */
public class CloseVisitRequest implements Parcelable {

    public static final Creator<CloseVisitRequest> CREATOR = new Creator<CloseVisitRequest>() {
        public CloseVisitRequest createFromParcel(Parcel in) {
            return new CloseVisitRequest(in);
        }

        public CloseVisitRequest[] newArray(int size) {
            return new CloseVisitRequest[size];
        }
    };

    private Location location;

    @JsonProperty("closingPhoto")
    private String closingPhotoId;

    public CloseVisitRequest() {

    }

    public CloseVisitRequest(Parcel in) {
        location = in.readParcelable(getClass().getClassLoader());
        closingPhotoId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
        dest.writeString(closingPhotoId);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getClosingPhotoId() {
        return closingPhotoId;
    }

    public void setClosingPhotoId(String closingPhotoId) {
        this.closingPhotoId = closingPhotoId;
    }
}
