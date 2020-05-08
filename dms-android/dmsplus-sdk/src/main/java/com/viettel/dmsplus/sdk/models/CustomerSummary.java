package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author PHAMHUNG
 * @since 3/3/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class CustomerSummary extends CustomerSimple {

    public static final Creator<CustomerSummary> CREATOR = new Creator<CustomerSummary>() {
        public CustomerSummary createFromParcel(Parcel source) {
            return new CustomerSummary(source);
        }

        public CustomerSummary[] newArray(int size) {
            return new CustomerSummary[size];
        }
    };

    private String mobile;
    private String phone;
    private String email;
    private String contact;
    private Location location;

    private BigDecimal outputLastMonth;
    private BigDecimal outputThisMonth;
    private long ordersThisMonth;

    private List<CustomerInfoRevenue> revenueLastThreeMonth;
    private List<CustomerInfoRecentOrder> lastFiveOrders;

    public CustomerSummary() {

    }

    public CustomerSummary(Parcel in) {
        super(in);

        this.mobile = in.readString();
        this.phone = in.readString();
        this.email = in.readString();
        this.contact = in.readString();
        this.location = in.readParcelable(Location.class.getClassLoader());

        this.outputLastMonth = (BigDecimal) in.readSerializable();
        this.outputThisMonth = (BigDecimal) in.readSerializable();
        this.ordersThisMonth = in.readLong();

        this.revenueLastThreeMonth = in.createTypedArrayList(CustomerInfoRevenue.CREATOR);
        this.lastFiveOrders = in.createTypedArrayList(CustomerInfoRecentOrder.CREATOR);
    }


    @Override
    public String toString() {
        return getName() + " " + getCode();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);

        dest.writeString(this.mobile);
        dest.writeString(this.phone);
        dest.writeString(this.email);
        dest.writeString(this.contact);
        dest.writeParcelable(this.location, 0);

        dest.writeSerializable(this.outputLastMonth);
        dest.writeSerializable(this.outputThisMonth);
        dest.writeLong(this.ordersThisMonth);

        dest.writeTypedList(revenueLastThreeMonth);
        dest.writeTypedList(lastFiveOrders);
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public BigDecimal getOutputLastMonth() {
        return outputLastMonth;
    }

    public void setOutputLastMonth(BigDecimal outputLastMonth) {
        this.outputLastMonth = outputLastMonth;
    }

    public BigDecimal getOutputThisMonth() {
        return outputThisMonth;
    }

    public void setOutputThisMonth(BigDecimal outputThisMonth) {
        this.outputThisMonth = outputThisMonth;
    }

    public long getOrdersThisMonth() {
        return ordersThisMonth;
    }

    public void setOrdersThisMonth(long ordersThisMonth) {
        this.ordersThisMonth = ordersThisMonth;
    }

    public List<CustomerInfoRevenue> getRevenueLastThreeMonth() {
        return revenueLastThreeMonth;
    }

    public void setRevenueLastThreeMonth(List<CustomerInfoRevenue> revenueLastThreeMonth) {
        this.revenueLastThreeMonth = revenueLastThreeMonth;
    }

    public List<CustomerInfoRecentOrder> getLastFiveOrders() {
        return lastFiveOrders;
    }

    public void setLastFiveOrders(List<CustomerInfoRecentOrder> lastFiveOrders) {
        this.lastFiveOrders = lastFiveOrders;
    }

}
