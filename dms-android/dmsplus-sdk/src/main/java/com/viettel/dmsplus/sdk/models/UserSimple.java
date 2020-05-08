package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;

/**
 * @author thanh
 * @since 10/13/15
 */
public class UserSimple extends IdDto {

    public static final Creator<UserSimple> CREATOR = new Creator<UserSimple>() {
        public UserSimple createFromParcel(Parcel in) {
            return new UserSimple(in);
        }

        public UserSimple[] newArray(int size) {
            return new UserSimple[size];
        }
    };

    private String username;
    private String fullname;

    public UserSimple() {
        super();
    }

    public UserSimple(Parcel in) {
        super(in);

        this.username = in.readString();
        this.fullname = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(username);
        dest.writeString(fullname);
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

}
