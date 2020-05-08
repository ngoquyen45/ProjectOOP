package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ Thanh on 3/10/2015.
 */
public class LocationHolder implements Parcelable {

    public static final Creator<LocationHolder> CREATOR = new Creator<LocationHolder>() {
        public LocationHolder createFromParcel(Parcel in) {
            return new LocationHolder(in);
        }

        public LocationHolder[] newArray(int size) {
            return new LocationHolder[size];
        }
    };

    private Location location;

    public LocationHolder() {

    }

    public LocationHolder(Location location) {
        this.location = location;
    }

    public LocationHolder(Parcel in) {
        location = in.readParcelable(this.getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(location, flags);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}
