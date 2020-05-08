package com.viettel.backend.domain;

import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "Config")
public class Config extends PO {

    private static final long serialVersionUID = 5202363354970843896L;

    public static enum OrderDateType {
        CREATED_DATE, APPROVED_DATE
    }

    // DEFAULT
    private String dateFormat;
    private String productPhoto;
    private Location location;

    // CALENDAR
    private int firstDayOfWeek;
    private int minimalDaysInFirstWeek;

    // SCHEDULE
    private boolean complexSchedule;
    private int numberWeekOfFrequency;

    // NUMBER DAY ORDER PENDING EXPIRE
    private int numberDayOrderPendingExpire;
    // ORDER DATE TYPE
    private OrderDateType orderDateType;

    private long visitDurationKPI;
    private double visitDistanceKPI;
    private boolean canEditCustomerLocation;

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    public String getProductPhoto() {
        return productPhoto;
    }

    public void setProductPhoto(String productPhoto) {
        this.productPhoto = productPhoto;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public int getMinimalDaysInFirstWeek() {
        return minimalDaysInFirstWeek;
    }

    public void setMinimalDaysInFirstWeek(int minimalDaysInFirstWeek) {
        this.minimalDaysInFirstWeek = minimalDaysInFirstWeek;
    }

    public boolean isComplexSchedule() {
        return complexSchedule;
    }

    public void setComplexSchedule(boolean complexSchedule) {
        this.complexSchedule = complexSchedule;
    }

    public int getNumberWeekOfFrequency() {
        return numberWeekOfFrequency;
    }

    public void setNumberWeekOfFrequency(int numberWeekOfFrequency) {
        this.numberWeekOfFrequency = numberWeekOfFrequency;
    }

    public int getNumberDayOrderPendingExpire() {
        return numberDayOrderPendingExpire;
    }

    public void setNumberDayOrderPendingExpire(int numberDayOrderPendingExpire) {
        this.numberDayOrderPendingExpire = numberDayOrderPendingExpire;
    }

    public OrderDateType getOrderDateType() {
        return orderDateType;
    }

    public void setOrderDateType(OrderDateType orderDateType) {
        this.orderDateType = orderDateType;
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

    @Transient
    public int getWeekIndex(SimpleDate date) {
        if (this.getNumberWeekOfFrequency() > 1) {
            int firstDayOfWeek = this.getFirstDayOfWeek();
            int minimalDaysInFirstWeek = this.getMinimalDaysInFirstWeek();
            // Tuan lam viec cua ngay hom nay tinh tu dau nam
            int weekOfToday = DateTimeUtils.getWeekOfYear(date, firstDayOfWeek, minimalDaysInFirstWeek);
            int weeekOfFrequency = this.getNumberWeekOfFrequency();
            // Thu tu cua tuan theo week duration
            int weekIndex = (weekOfToday % weeekOfFrequency) + 1;
            return weekIndex;
        } else {
            return 0;
        }
    }

}
