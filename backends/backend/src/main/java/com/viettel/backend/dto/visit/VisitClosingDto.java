package com.viettel.backend.dto.visit;

import java.io.Serializable;

import com.viettel.backend.util.entity.Location;

public class VisitClosingDto implements Serializable {
    
    private static final long serialVersionUID = -522893756957841955L;
    
    private String closingPhoto;
    private Location location;

    public String getClosingPhoto() {
        return closingPhoto;
    }

    public void setClosingPhoto(String closingPhoto) {
        this.closingPhoto = closingPhoto;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }

}
