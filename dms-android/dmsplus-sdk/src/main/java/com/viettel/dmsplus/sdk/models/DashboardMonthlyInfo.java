package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Using Parcelable instead of Serializable for better performance
 * 
 * @author ThanhNV60
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DashboardMonthlyInfo implements Parcelable {

    public static final Creator<DashboardMonthlyInfo> CREATOR = new Creator<DashboardMonthlyInfo>() {
        public DashboardMonthlyInfo createFromParcel(Parcel in) {
            return new DashboardMonthlyInfo(in);
        }

        public DashboardMonthlyInfo[] newArray(int size) {
            return new DashboardMonthlyInfo[size];
        }
    };

    private DashboardInfoItem revenue;
    private DashboardInfoItem productivity;
    private DashboardInfoItem visit;
    @JsonProperty("day")
    private DashboardInfoItem saleDays;

    public DashboardMonthlyInfo() {

    }

    private DashboardMonthlyInfo(Parcel in) {
        ClassLoader classLoader = getClass().getClassLoader();
        revenue = in.readParcelable(classLoader);
        productivity = in.readParcelable(classLoader);
        visit = in.readParcelable(classLoader);
        saleDays = in.readParcelable(classLoader);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(revenue, flags);
        dest.writeParcelable(productivity, flags);
        dest.writeParcelable(visit, flags);
        dest.writeParcelable(saleDays, flags);
    }

    public DashboardInfoItem getRevenue() {
        return revenue;
    }

    public void setRevenue(DashboardInfoItem revenue) {
        this.revenue = revenue;
    }

    public DashboardInfoItem getProductivity() {
        return productivity;
    }

    public void setProductivity(DashboardInfoItem productivity) {
        this.productivity = productivity;
    }

    public DashboardInfoItem getVisit() {
        return visit;
    }

    public void setVisit(DashboardInfoItem visit) {
        this.visit = visit;
    }

    public DashboardInfoItem getSaleDays() {
        return saleDays;
    }

    public void setSaleDays(DashboardInfoItem saleDays) {
        this.saleDays = saleDays;
    }

}
