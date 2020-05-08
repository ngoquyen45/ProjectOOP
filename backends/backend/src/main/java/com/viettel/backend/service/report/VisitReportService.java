package com.viettel.backend.service.report;

import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.DistributorVisitResultDto;
import com.viettel.backend.dto.report.SalesmanVisitResultDto;
import com.viettel.backend.dto.report.VisitResultDailyDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface VisitReportService {

    public ListDto<VisitResultDailyDto> getVisitResultDaily(UserLogin userLogin, int month, int year);

    public ListDto<DistributorVisitResultDto> getDistributorVisitResult(UserLogin userLogin, int month, int year);

    public ListDto<SalesmanVisitResultDto> getSalesmanVisitResult(UserLogin userLogin, int month, int year);

}
