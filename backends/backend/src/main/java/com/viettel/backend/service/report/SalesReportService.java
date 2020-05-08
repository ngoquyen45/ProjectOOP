package com.viettel.backend.service.report;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.DistributorSalesResultDto;
import com.viettel.backend.dto.report.ProductSalesResultDto;
import com.viettel.backend.dto.report.SalesResultDailyDto;
import com.viettel.backend.dto.report.SalesmanSalesResultDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface SalesReportService {

    public ListDto<SalesResultDailyDto> getSalesResultDaily(UserLogin userLogin, int month, int year);
    
    public ListDto<DistributorSalesResultDto> getDistributorSalesResult(UserLogin userLogin, int month, int year);
    
    public ListDto<ProductSalesResultDto> getProductSalesResult(UserLogin userLogin, int month, int year, String productCategoryId);
    
    public ListDto<SalesmanSalesResultDto> getSalesmanSalesResult(UserLogin userLogin, int month, int year);

}
