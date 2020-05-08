package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CategorySimple extends IdDto implements Serializable {

    public static final Creator<CategorySimple> CREATOR = new Creator<CategorySimple>() {
        public CategorySimple createFromParcel(Parcel in) {
            return new CategorySimple(in);
        }

        public CategorySimple[] newArray(int size) {
            return new CategorySimple[size];
        }
    };

    private String name;
    private String code;

    public CategorySimple() {
        super();
    }

    public CategorySimple(String id, String name, String code) {
        super();
        this.setId(id);
        this.setName(name);
        this.setCode(code);
    }

    public CategorySimple(Parcel in) {
        super(in);

        this.name = in.readString();
        this.code = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(name);
        dest.writeString(code);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
