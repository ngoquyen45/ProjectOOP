package com.viettel.backend.service.visit;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.visit.CustomerForVisitDto;
import com.viettel.backend.dto.visit.CustomerSummaryDto;
import com.viettel.backend.dto.visit.VisitClosingDto;
import com.viettel.backend.dto.visit.VisitEndDto;
import com.viettel.backend.dto.visit.VisitInfoDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.util.entity.Location;

public interface VisitService {

    // CUSTOMER
    public ListDto<CustomerForVisitDto> getCustomersForVisit(UserLogin userLogin, Boolean plannedToday, String search);
    
    public CustomerSummaryDto getCustomerSummary(UserLogin userLogin, String id);
    
    public void updatePhone(UserLogin userLogin, String id, String phone);
    
    public void updateMobile(UserLogin userLogin, String id, String mobile);
    
    public void updateLocation(UserLogin userLogin, String id, Location locationDto);
    
    // VISIT
    /** Salesman start visit a customer at a location @return visit id */
    public IdDto startVisit(UserLogin userLogin, String customerId, Location locationDto);

    /** Salesman end a visit @return visit info */
    public VisitInfoDto endVisit(UserLogin userLogin, String visitId, VisitEndDto dto);

    /** Salesman mark as a customer is closed @return visit id */
    public IdDto markAsClosed(UserLogin userLogin, String customerId, VisitClosingDto dto);

    /** get today visit info of salesman's customer */
    public VisitInfoDto getVisitedTodayInfo(UserLogin userLogin, String customerId);

}
