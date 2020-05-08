package com.viettel.backend.service.config;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.ClientConfigDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface ClientConfigService {

    public ClientConfigDto get(UserLogin userLogin);
    
    public IdDto set(UserLogin userLogin, ClientConfigDto dto);
    
}
