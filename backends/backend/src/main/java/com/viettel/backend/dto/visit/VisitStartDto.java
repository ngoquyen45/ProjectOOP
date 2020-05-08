package com.viettel.backend.dto.visit;

import java.io.Serializable;

import com.viettel.backend.util.entity.Location;

public class VisitStartDto implements Serializable {

    private static final long serialVersionUID = 865188777179374746L;

    private Location location;

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

}
