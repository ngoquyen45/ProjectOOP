package com.viettel.backend.dto.visit;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.order.OrderSimpleDto;

public class VisitInfoListDto extends OrderSimpleDto {

    private static final long serialVersionUID = -4244621688895579808L;

    private boolean hasOrder;

    private int visitStatus;
    private boolean closed;
    private String photo;

    private String startTime;
    private String endTime;

    private long duration;
    private boolean errorDuration;

    private int locationStatus;
    private Double distance;

    public VisitInfoListDto(Visit visit) {
        super(visit);

        this.hasOrder = visit.isOrder();
        this.visitStatus = visit.getVisitStatus();
        this.closed = visit.isClosed();
        this.photo = visit.getPhoto();
        this.locationStatus = visit.getLocationStatus();
        this.distance = visit.getDistance();
        this.startTime = visit.getStartTime() != null ? visit.getStartTime().getIsoTime() : null;
        this.endTime = visit.getEndTime() != null ? visit.getEndTime().getIsoTime() : null;
        this.duration = visit.getDuration();
        this.errorDuration = visit.isErrorDuration();
    }

    public boolean isHasOrder() {
        return hasOrder;
    }

    public void setHasOrder(boolean hasOrder) {
        this.hasOrder = hasOrder;
    }

    public int getVisitStatus() {
        return visitStatus;
    }

    public void setVisitStatus(int visitStatus) {
        this.visitStatus = visitStatus;
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

    public UserSimpleDto getSalesman() {
        return super.getCreatedBy();
    }

}
