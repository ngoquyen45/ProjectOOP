package com.viettel.backend.util.entity;

import java.io.Serializable;

import org.springframework.util.Assert;

public class Location implements Serializable {

    private static final long serialVersionUID = 5038209025937567636L;

    private double latitude;
    private double longitude;

    public Location() {
        this(-1, -1);
    }

    public Location(double latitude, double longitude) {
        super();

        this.latitude = latitude;
        this.longitude = longitude;
    }
    
    public Location(double[] location) {
        super();

        Assert.notNull(location);
        Assert.isTrue(location.length == 2);
        
        this.longitude = location[0];
        this.latitude = location[1];
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

}
