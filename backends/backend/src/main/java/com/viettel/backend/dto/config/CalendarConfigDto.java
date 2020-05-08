package com.viettel.backend.dto.config;

import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.dto.common.DTOSimple;
import com.viettel.backend.util.entity.SimpleDate;

public class CalendarConfigDto extends DTOSimple {

    private static final long serialVersionUID = 1786366439733982764L;

    private List<Integer> workingDays;
    private List<String> holidays;
    
    public CalendarConfigDto() {
        super((String) null);
    }
    
    public CalendarConfigDto(CalendarConfig calendarConfig) {
        super(calendarConfig);
        
        this.workingDays = calendarConfig.getWorkingDays();
        if (calendarConfig.getHolidays() != null) {
            this.holidays = new ArrayList<String>(calendarConfig.getHolidays().size());
            for (SimpleDate holiday : calendarConfig.getHolidays()) {
                this.holidays.add(holiday.getIsoDate());
            }
        }
    }

    public List<Integer> getWorkingDays() {
        return workingDays;
    }

    public void setWorkingDays(List<Integer> workingDays) {
        this.workingDays = workingDays;
    }

    public List<String> getHolidays() {
        return holidays;
    }

    public void setHolidays(List<String> holidays) {
        this.holidays = holidays;
    }


}
