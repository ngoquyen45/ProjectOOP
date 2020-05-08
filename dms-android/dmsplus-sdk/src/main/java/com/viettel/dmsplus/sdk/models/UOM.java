package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Created by Thanh on 14/04/2015.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UOM implements Parcelable,Serializable {

    public static final Creator<UOM> CREATOR = new Creator<UOM>() {
        public UOM createFromParcel(Parcel in) {
            return new UOM(in);
        }

        public UOM[] newArray(int size) {
            return new UOM[size];
        }
    };

    private String id;
    private String code;
    private String name;

    public UOM() {

    }

    public UOM(Parcel in) {
        id = in.readString();
        code = in.readString();
        name = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(code);
        dest.writeString(name);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
