package com.viettel.backend.service.config;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.SystemConfigDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface SystemConfigService {

    public SystemConfigDto get(UserLogin userLogin);
    
    public IdDto set(UserLogin userLogin, SystemConfigDto dto);
    
}
