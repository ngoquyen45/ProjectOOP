package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StringDto implements Parcelable {

    public static final Creator<StringDto> CREATOR = new Creator<StringDto>() {
        public StringDto createFromParcel(Parcel in) {
            return new StringDto(in);
        }

        public StringDto[] newArray(int size) {
            return new StringDto[size];
        }
    };

    private String content;

    public StringDto() {
        super();
    }

    public StringDto(String content) {
        super();
        this.content = content;
    }

    public StringDto(Parcel in) {
        super();
        this.content = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(content);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
}
