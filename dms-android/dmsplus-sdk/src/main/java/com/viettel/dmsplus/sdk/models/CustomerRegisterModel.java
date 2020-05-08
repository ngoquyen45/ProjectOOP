package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @author PHAMHUNG
 * @since 11/3/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerRegisterModel implements Parcelable {

    public static final Creator<CustomerRegisterModel> CREATOR = new Creator<CustomerRegisterModel>() {
        @Override
        public CustomerRegisterModel createFromParcel(Parcel parcel) {
            return new CustomerRegisterModel(parcel);
        }

        @Override
        public CustomerRegisterModel[] newArray(int i) {
            return new CustomerRegisterModel[i];
        }
    };

    private String name;
    private String code;
    private String address;
    private String phone;
    private String mobile;
    private String contact;
    private String email;
    private String description;
    private Location location;
    private String customerTypeId;
    private String[] photos;
    private String areaId;

    public CustomerRegisterModel() {

    }

    public CustomerRegisterModel(Parcel in) {
        name = in.readString();
        code = in.readString();
        address = in.readString();
        phone = in.readString();
        mobile = in.readString();
        contact = in.readString();
        email = in.readString();
        description = in.readString();
        location = in.readParcelable(Location.class.getClassLoader());
        customerTypeId = in.readString();
        areaId = in.readString();
        in.readStringArray(photos);
    }

    @Override
         public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flag) {
        parcel.writeString(name);
        parcel.writeString(code);
        parcel.writeString(address);
        parcel.writeString(phone);
        parcel.writeString(mobile);
        parcel.writeString(contact);
        parcel.writeString(email);
        parcel.writeString(description);
        parcel.writeParcelable(location, flag);
        parcel.writeString(customerTypeId);
        parcel.writeString(areaId);
        parcel.writeStringArray(photos);
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCustomerTypeId() {
        return customerTypeId;
    }

    public void setCustomerTypeId(String customerTypeId) {
        this.customerTypeId = customerTypeId;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String[] getPhotos() {
        return photos;
    }

    public void setPhotos(String[] photos) {
        this.photos = photos;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return getName();
    }
}
