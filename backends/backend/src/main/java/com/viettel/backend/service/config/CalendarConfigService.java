package com.viettel.backend.service.config;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.CalendarConfigDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface CalendarConfigService {

    public CalendarConfigDto get(UserLogin userLogin);
    
    public IdDto set(UserLogin userLogin, CalendarConfigDto dto);
    
}
