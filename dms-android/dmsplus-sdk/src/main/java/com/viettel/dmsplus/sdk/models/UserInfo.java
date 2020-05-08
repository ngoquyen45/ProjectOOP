package com.viettel.dmsplus.sdk.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.viettel.dmsplus.sdk.Role;

/**
 * @author PHAMHUNG
 * @since 8/17/2015
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserInfo implements Parcelable {
    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        public UserInfo createFromParcel(Parcel in) {
            return new UserInfo(in);
        }

        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };

    private String id;
    private String username;
    private String fullname;
    private String clientName;
    @JsonProperty("roleCode")
    private Role role;
    private Location location;
    private String dateFormat;
    private boolean canEditCustomerLocation;
    private double visitDistanceKPI;
    private long visitDurationKPI;
    private boolean vanSales;

    public UserInfo() {
    }

    public UserInfo(Parcel in) {
        id = in.readString();
        username = in.readString();
        fullname = in.readString();
        role = in.readParcelable(Role.class.getClassLoader());
        clientName = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(fullname);
        dest.writeParcelable(role, flags);
        dest.writeString(clientName);
    }
        //region get/set
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public boolean isCanEditCustomerLocation() {
        return canEditCustomerLocation;
    }

    public void setCanEditCustomerLocation(boolean canEditCustomerLocation) {
        this.canEditCustomerLocation = canEditCustomerLocation;
    }

    public double getVisitDistanceKPI() {
        return visitDistanceKPI;
    }

    public void setVisitDistanceKPI(double visitDistanceKPI) {
        this.visitDistanceKPI = visitDistanceKPI;
    }

    public long getVisitDurationKPI() {
        return visitDurationKPI;
    }

    public void setVisitDurationKPI(long visitDurationKPI) {
        this.visitDurationKPI = visitDurationKPI;
    }

    public boolean isVanSales() {
        return vanSales;
    }

    public void setVanSales(boolean vanSales) {
        this.vanSales = vanSales;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }
    //endregion
}
