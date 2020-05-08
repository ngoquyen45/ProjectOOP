package com.viettel.backend.dto.visit;

import java.math.BigDecimal;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.dto.common.DTOSimple;

public class VisitSimpleInfoDto extends DTOSimple {
    
    private static final long serialVersionUID = 1734477498344542962L;
    
    private boolean hasOrder;
    private boolean closed;
    private String photo;
    private String startTime;
    private String endTime;
    private long duration;
    private boolean errorDuration;
    private int locationStatus;
    private Double distance;
    private BigDecimal grandTotal;
    private int approveStatus;
    
    public VisitSimpleInfoDto(Visit visit) {
        super(visit);
        
        this.hasOrder = visit.isOrder();
        this.closed = visit.isClosed();
        this.photo = visit.getPhoto();
        this.locationStatus = visit.getLocationStatus();
        this.distance = visit.getDistance();
        this.startTime = visit.getStartTime() != null ? visit.getStartTime().getIsoTime() : null;
        this.endTime = visit.getEndTime() != null ? visit.getEndTime().getIsoTime() : null;
        this.duration = visit.getDuration();
        this.errorDuration = visit.isErrorDuration();
        this.grandTotal = visit.getGrandTotal();
        this.approveStatus = visit.getApproveStatus();
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

    public boolean isClosed() {
        return closed;
    }

    public void setClosed(boolean closed) {
        this.closed = closed;
    }

    public String getPhoto() {
        return photo;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isErrorDuration() {
        return errorDuration;
    }

    public void setErrorDuration(boolean errorDuration) {
        this.errorDuration = errorDuration;
    }

    public int getLocationStatus() {
        return locationStatus;
    }

    public void setLocationStatus(int locationStatus) {
        this.locationStatus = locationStatus;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public BigDecimal getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(BigDecimal grandTotal) {
        this.grandTotal = grandTotal;
    }

    public int getApproveStatus() {
        return approveStatus;
    }

    public void setApproveStatus(int approveStatus) {
        this.approveStatus = approveStatus;
    }
    
}
