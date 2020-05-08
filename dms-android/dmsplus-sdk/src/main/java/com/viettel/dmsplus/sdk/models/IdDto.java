package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class IdDto implements Parcelable, Serializable {

    public static final Creator<IdDto> CREATOR = new Creator<IdDto>() {
        public IdDto createFromParcel(Parcel in) {
            return new IdDto(in);
        }

        public IdDto[] newArray(int size) {
            return new IdDto[size];
        }
    };

    private String id;
    
    public IdDto() {
        super();
    }

    public IdDto(Parcel in) {
        super();
        this.id = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
