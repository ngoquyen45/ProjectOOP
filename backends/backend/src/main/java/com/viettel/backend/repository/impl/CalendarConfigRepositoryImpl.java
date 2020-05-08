package com.viettel.backend.repository.impl;

import java.util.LinkedList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.common.CacheRepository;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class CalendarConfigRepositoryImpl extends AbstractRepository<CalendarConfig> implements
        CalendarConfigRepository {

    @Autowired
    private CacheRepository cacheRepository;

    @Override
    public CalendarConfig getCalendarConfig(ObjectId clientId) {
        CalendarConfig calendarConfig = cacheRepository.getCalendarConfigCache(clientId);

        if (calendarConfig == null) {
            calendarConfig = _getFirst(clientId, false, true, null, null);
            if (calendarConfig != null) {
                cacheRepository.setCalendarConfigCache(clientId, calendarConfig);
            }
        }

        return calendarConfig;
    }

    @Override
    public CalendarConfig save(ObjectId clientId, CalendarConfig domain) {
        Assert.notNull(domain);

        CalendarConfig calendarConfig = _save(clientId, domain);
        cacheRepository.setCalendarConfigCache(clientId, calendarConfig);
        cacheRepository.clearWorkingDaysCache(clientId);
        return calendarConfig;
    }

    @Override
    public List<SimpleDate> getWorkingDays(ObjectId clientId, Period period) {
        List<SimpleDate> workingDays = cacheRepository.getWorkingDays(clientId, period);
        if (workingDays != null && workingDays.size() > 0) {
            return workingDays;
        }
        
        CalendarConfig calendarConfig = getCalendarConfig(clientId);
        if (calendarConfig == null) {
            throw new UnsupportedOperationException("calendar config not found");
        }

        workingDays = new LinkedList<SimpleDate>();
        SimpleDate temp = period.getFromDate();
        while (temp.compareTo(period.getToDate()) < 0) {
            if (calendarConfig != null) {
                if (isWorkingDay(calendarConfig, temp)) {
                    workingDays.add(temp);
                }
            } else {
                workingDays.add(temp);
            }

            temp = DateTimeUtils.addDays(temp, 1);
        }

        cacheRepository.setWorkingDays(clientId, period, workingDays);
        
        return workingDays;
    }

    @Override
    public SimpleDate getPreviousWorkingDay(ObjectId clientId, SimpleDate date) {
        CalendarConfig calendarConfig = getCalendarConfig(clientId);
        if (calendarConfig == null) {
            throw new UnsupportedOperationException("calendar config not found");
        }
        
        int i = 0;
        SimpleDate temp = date;
        while(true) {
            if (i > 365) {
                throw new UnsupportedOperationException("not found working day from one year");
            }
            
            temp = DateTimeUtils.addDays(temp, -1);
            if (isWorkingDay(calendarConfig, temp)) {
                return temp;
            }
            i++;
        } 
    }

    // PRIVATE
    private boolean isWorkingDay(CalendarConfig calendarConfig, SimpleDate _date) {
        Assert.notNull(calendarConfig);
        Assert.notNull(_date);

        int day = _date.getDayOfWeek();
        int date = _date.getDate();
        int month = _date.getMonth();
        int year = _date.getYear();

        // NGAY NGHI DAC BIET
        if (calendarConfig.getHolidays() != null) {
            for (SimpleDate simpleDate : calendarConfig.getHolidays()) {
                if (simpleDate.getDate() == date && simpleDate.getMonth() == month && simpleDate.getYear() == year) {
                    return false;
                }
            }
        }

        // BO WEEKEND
        if (calendarConfig.getWorkingDays() != null && calendarConfig.getWorkingDays().contains(day)) {
            return true;
        }

        return false;
    }

}
