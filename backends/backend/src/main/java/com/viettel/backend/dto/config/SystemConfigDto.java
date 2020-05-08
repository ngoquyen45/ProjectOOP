package com.viettel.backend.dto.config;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.dto.common.DTOSimple;
import com.viettel.backend.util.entity.Location;
import com.viettel.backend.util.entity.SimpleFile;

public class SystemConfigDto extends DTOSimple {

    private static final long serialVersionUID = 4618363876249230856L;

    // DEFAULT
    private String dateFormat;
    private String productPhoto;

    // CALENDAR
    private int firstDayOfWeek;
    private int minimalDaysInFirstWeek;

    // SCHEDULE
    private boolean complexSchedule;
    private int numberWeekOfFrequency;

    // ORDER DATE TYPE
    private OrderDateType orderDateType;
    // NUMBER DAY ORDER PENDING EXPIRE
    private int numberDayOrderPendingExpire;

    // IMPORT TEMPLATE
    private SimpleFile importCustomerTemplate;
    private SimpleFile importProductTemplate;
    
    private Location location;

    public SystemConfigDto() {
        super((String) null);
    }

    public SystemConfigDto(Config config) {
        super(config);

        this.setProductPhoto(config.getProductPhoto());
        this.setDateFormat(config.getDateFormat());

        this.setFirstDayOfWeek(config.getFirstDayOfWeek());
        this.setMinimalDaysInFirstWeek(config.getMinimalDaysInFirstWeek());

        this.setComplexSchedule(config.isComplexSchedule());
        this.setNumberWeekOfFrequency(config.getNumberWeekOfFrequency());

        this.setOrderDateType(config.getOrderDateType());
        this.setNumberDayOrderPendingExpire(config.getNumberDayOrderPendingExpire());

        this.setLocation(config.getLocation());
    }

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

    public SimpleFile getImportCustomerTemplate() {
        return importCustomerTemplate;
    }

    public void setImportCustomerTemplate(SimpleFile importCustomerTemplate) {
        this.importCustomerTemplate = importCustomerTemplate;
    }

    public SimpleFile getImportProductTemplate() {
        return importProductTemplate;
    }

    public void setImportProductTemplate(SimpleFile importProductTemplate) {
        this.importProductTemplate = importProductTemplate;
    }
    
    public Location getLocation() {
        return location;
    }
    
    public void setLocation(Location location) {
        this.location = location;
    }

}
