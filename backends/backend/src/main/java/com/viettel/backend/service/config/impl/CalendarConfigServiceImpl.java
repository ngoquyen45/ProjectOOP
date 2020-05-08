package com.viettel.backend.service.config.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.CalendarConfigDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.config.CalendarConfigService;
import com.viettel.backend.util.entity.SimpleDate;

@RolePermission(value={ Role.ADMIN })
@Service
public class CalendarConfigServiceImpl extends AbstractService implements CalendarConfigService {

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;
    
    @Override
    public CalendarConfigDto get(UserLogin userLogin) {
        CalendarConfig calendarConfig = calendarConfigRepository.getCalendarConfig(userLogin.getClientId());
        
        if (calendarConfig == null) {
            return new CalendarConfigDto();
        } else {
            return new CalendarConfigDto(calendarConfig);
        }
    }

    @Override
    public IdDto set(UserLogin userLogin, CalendarConfigDto dto) {
        BusinessAssert.notNull(dto);
        BusinessAssert.notEmpty(dto.getWorkingDays());
        
        CalendarConfig calendarConfig = calendarConfigRepository.getCalendarConfig(userLogin.getClientId());
        
        if (calendarConfig == null) {
            calendarConfig = new CalendarConfig();
            initPOWhenCreate(CalendarConfig.class, userLogin, calendarConfig);
        }
        
        calendarConfig.setWorkingDays(dto.getWorkingDays());
        if (dto.getHolidays() != null) {
            List<SimpleDate> holidays = new ArrayList<SimpleDate>(dto.getHolidays().size());
            for (String holiday : dto.getHolidays()) {
                holidays.add(getMandatoryIsoDate(holiday));
            }
            
            Collections.sort(holidays);
            
            calendarConfig.setHolidays(holidays);
        }
        
        calendarConfig = calendarConfigRepository.save(userLogin.getClientId(), calendarConfig);
        
        return new IdDto(calendarConfig);
    }

}
