package com.viettel.backend.dto.config;

import com.viettel.backend.domain.Config;
import com.viettel.backend.dto.common.DTOSimple;
import com.viettel.backend.util.entity.Location;

public class ClientConfigDto extends DTOSimple {

    private static final long serialVersionUID = 4618363876249230856L;

    private long visitDurationKPI;
    private double visitDistanceKPI;
    private boolean canEditCustomerLocation;
    private Location location;

    public ClientConfigDto() {
        super((String) null);
    }

    public ClientConfigDto(Config config) {
        super(config);

        this.visitDurationKPI = config.getVisitDurationKPI();
        this.visitDistanceKPI = config.getVisitDistanceKPI();
        this.canEditCustomerLocation = config.isCanEditCustomerLocation();
        this.location = config.getLocation();
    }

    public long getVisitDurationKPI() {
        return visitDurationKPI;
    }

    public void setVisitDurationKPI(long visitDurationKPI) {
        this.visitDurationKPI = visitDurationKPI;
    }

    public double getVisitDistanceKPI() {
        return visitDistanceKPI;
    }

    public void setVisitDistanceKPI(double visitDistanceKPI) {
        this.visitDistanceKPI = visitDistanceKPI;
    }

    public boolean isCanEditCustomerLocation() {
        return canEditCustomerLocation;
    }

    public void setCanEditCustomerLocation(boolean canEditCustomerLocation) {
        this.canEditCustomerLocation = canEditCustomerLocation;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

}
