package com.viettel.dmsplus.sdk;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @author Thanh
 */
public enum Role implements Parcelable {

    SALESMAN("SM", "salesman"), STORECHECKER("SC", "store-checker");

    private String value;

    private String urlPrefix;

    Role(String value, String urlPrefix) {
        this.value = value;
        this.urlPrefix = urlPrefix;
    }

    public String getUrlPrefix() {
        return urlPrefix;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        dest.writeInt(ordinal());
    }

    public static final Creator<Role> CREATOR = new Creator<Role>() {
        @Override
        public Role createFromParcel(final Parcel source) {
            return Role.values()[source.readInt()];
        }

        @Override
        public Role[] newArray(final int size) {
            return new Role[size];
        }
    };

}