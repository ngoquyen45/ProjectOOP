package com.viettel.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface CalendarConfigRepository {

    public CalendarConfig getCalendarConfig(ObjectId clientId);

    public CalendarConfig save(ObjectId clientId, CalendarConfig domain);
    
    public List<SimpleDate> getWorkingDays(ObjectId clientId, Period period);
    
    public SimpleDate getPreviousWorkingDay(ObjectId clientId, SimpleDate date);

}
