package com.viettel.backend.service.visit;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.dto.visit.VisitInfoListDto;
import com.viettel.backend.dto.visit.VisitTodaySummary;
import com.viettel.backend.oauth2.core.UserLogin;

public interface VisitMonitoringService {

    public ListDto<CustomerForVisitDto> getCustomersTodayBySalesman(UserLogin userLogin, String _salesmanId);

    public ListDto<VisitInfoListDto> getVisitsToday(UserLogin userLogin, String _distributorId, String _salesmanId,
            Pageable pageable);

    public ListDto<VisitInfoListDto> getVisits(UserLogin userLogin, String _distributorId, String _salesmanId,
            String fromDate, String toDate, Pageable pageable);

    public VisitInfoDto getVisitInfoById(UserLogin userLogin, String id);

    public VisitTodaySummary getVisitTodaySummary(UserLogin userLogin, String _distributorId, String _salesmanId);

}
