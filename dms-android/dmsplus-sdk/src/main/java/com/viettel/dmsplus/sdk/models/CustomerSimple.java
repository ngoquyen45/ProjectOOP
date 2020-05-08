package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerSimple extends CategorySimple implements Serializable {

    public static final Creator<CustomerSimple> CREATOR = new Creator<CustomerSimple>() {
        @Override
        public CustomerSimple createFromParcel(Parcel parcel) {
            return new CustomerSimple(parcel);
        }

        @Override
        public CustomerSimple[] newArray(int i) {
            return new CustomerSimple[i];
        }
    };

    private String address;
    private CategorySimple area;
    private CategorySimple customerType;

    private boolean joinDP;
    private Integer dpOption;

    public CustomerSimple() {
        super();
    }

    public CustomerSimple(Parcel in) {
        super(in);

        this.address = in.readString();
        this.area = in.readParcelable(this.getClass().getClassLoader());
        this.customerType = in.readParcelable(this.getClass().getClassLoader());

        this.joinDP = in.readByte() != 0;
        int option = in.readInt();
        this.dpOption = option > 0 ? option : null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(address);
        dest.writeParcelable(area, flags);
        dest.writeParcelable(customerType, flags);

        dest.writeByte((byte) (joinDP ? 1 : 0));
        dest.writeInt(dpOption != null ? dpOption : -1);
    }

    public CategorySimple getArea() {
        return area;
    }

    public void setArea(CategorySimple area) {
        this.area = area;
    }

    public CategorySimple getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CategorySimple customerType) {
        this.customerType = customerType;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isJoinDP() {
        return joinDP;
    }

    public void setJoinDP(boolean joinDP) {
        this.joinDP = joinDP;
    }

    public Integer getDpOption() {
        return dpOption;
    }

    public void setDpOption(Integer dpOption) {
        this.dpOption = dpOption;
    }
}
