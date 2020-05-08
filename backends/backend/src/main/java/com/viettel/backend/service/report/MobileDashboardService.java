package com.viettel.backend.service.report;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.order.OrderSimpleDto;
import com.viettel.backend.dto.report.CustomerSalesResultDto;
import com.viettel.backend.dto.report.MobileDashboardDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface MobileDashboardService {

    public MobileDashboardDto getMobileDashboard(UserLogin userLogin);
    
    public ListDto<CustomerSalesResultDto> getCustomerSalesResultsThisMonth(UserLogin userLogin);
    
    public ListDto<OrderSimpleDto> getOrderByCustomerThisMonth(UserLogin userLogin, String customerId);
    
    public ListDto<SalesResultDailyDto> getSalesResultDailyThisMonth(UserLogin userLogin);
    
    public ListDto<OrderSimpleDto> getOrderByDateThisMonth(UserLogin userLogin, String date);
    
}
