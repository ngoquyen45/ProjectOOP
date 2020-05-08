package com.viettel.backend.service.report;

import com.viettel.backend.dto.report.WebDashboardDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface WebDashboardService {

    public WebDashboardDto getWebDashboard(UserLogin userLogin);
    
}
