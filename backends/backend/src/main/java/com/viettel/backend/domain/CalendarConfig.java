package com.viettel.backend.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "CalendarConfig")
public class CalendarConfig extends PO {

    private static final long serialVersionUID = 7763979551635207290L;

    private List<Integer> workingDays;
    private List<SimpleDate> holidays;

    public List<Integer> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<Integer> workingDays) {
        this.workingDays = workingDays;
    }

    public List<SimpleDate> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<SimpleDate> holidays) {
        this.holidays = holidays;
    }

}
