package com.viettel.backend.service.report;

import com.viettel.backend.dto.report.PerformanceDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface PerformanceService {
    
    public PerformanceDto getPerformanceBySalesman(UserLogin userLogin, String salesmanId, int month, int year);
    
}
