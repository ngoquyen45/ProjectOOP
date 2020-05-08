package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerForVisit extends CustomerSimple implements Comparable<CustomerForVisit>, Serializable {

    public static final Creator<CustomerForVisit> CREATOR = new Creator<CustomerForVisit>() {
        public CustomerForVisit createFromParcel(Parcel in) {
            return new CustomerForVisit(in);
        }

        public CustomerForVisit[] newArray(int size) {
            return new CustomerForVisit[size];
        }
    };


    private boolean planned;
    private int visitStatus;
    private int seqNo;
    private Location location;
    private String phone;
    private String mobile;
    private String contact;
    private String email;
    private VisitSimpleInfoDto visitInfo;

    public CustomerForVisit() {
        super();
    }

    public CustomerForVisit(Parcel in) {
        super(in);

        this.planned = in.readByte() != 0;
        this.visitStatus = in.readInt();
        this.seqNo = in.readInt();
        this.location = in.readParcelable(this.getClass().getClassLoader());
        this.phone = in.readString();
        this.mobile = in.readString();
        this.contact = in.readString();
        this.email = in.readString();
        this.visitInfo = in.readParcelable(this.getClass().getClassLoader());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flag) {
        super.writeToParcel(dest, flag);

        dest.writeByte((byte) (planned ? 1 : 0));
        dest.writeInt(visitStatus);
        dest.writeInt(seqNo);
        dest.writeParcelable(location, flag);
        dest.writeString(phone);
        dest.writeString(mobile);
        dest.writeString(contact);
        dest.writeString(email);
        dest.writeParcelable(visitInfo, flag);
    }

    /**
     * Compare base on isplanned
     * true: Noi tuyen
     * false : ngoai tuyen
     * Then compare base on status
     * 0: Dang tham 1:Chua tham 2: Da tham
     * 0 - 1 : ngoai tuyen
     */
    @Override
    public int compareTo(@NonNull CustomerForVisit another) {
        if (this.isPlanned() != another.isPlanned()) {
            if (this.isPlanned()) {
                return -1;
            } else {
                return 1;
            }
        }
        if (this.getVisitStatus() != another.getVisitStatus()) {
            return this.getVisitStatus() - another.getVisitStatus();
        } else {
            return this.getSeqNo() - another.getSeqNo();
        }

    }

    public boolean isPlanned() {
        return planned;
    }

    public void setPlanned(boolean planned) {
        this.planned = planned;
    }

    public int getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(int visitStatus) {
        this.visitStatus = visitStatus;
    }

    public int getSeqNo() {
        return seqNo;
    }

    public void setSeqNo(int seqNo) {
        this.seqNo = seqNo;
    }

    public VisitSimpleInfoDto getVisitInfo() {
        return visitInfo;
    }

    public void setVisitInfo(VisitSimpleInfoDto visitInfo) {
        this.visitInfo = visitInfo;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
