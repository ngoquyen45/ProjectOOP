package com.viettel.backend.service.schedule;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.schedule.CustomerScheduleCreateDto;
import com.viettel.backend.dto.schedule.CustomerScheduleDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface CustomerScheduleService {

    public ListDto<CustomerScheduleDto> getCustomerSchedules(UserLogin userLogin, String distributorId,
            boolean searchByRoute, String routeId, String search, Integer dayOfWeek, Pageable pageable);
    
    public CustomerScheduleDto getCustomerSchedule(UserLogin userLogin, String customerId);

    public void saveCustomerScheduleByDistributor(UserLogin userLogin, String _distributorId,
            ListDto<CustomerScheduleCreateDto> list);
    
    public void saveCustomerSchedule(UserLogin userLogin, CustomerScheduleCreateDto dto);

}
