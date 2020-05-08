package com.viettel.backend.service.config.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.ClientConfigDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.config.ClientConfigService;

@RolePermission(value={ Role.ADMIN, Role.SUPER_ADMIN })
@Service
public class ClientConfigServiceImpl extends AbstractService implements ClientConfigService {

    @Autowired
    private ConfigRepository configRepository;
    
    @Override
    public ClientConfigDto get(UserLogin userLogin) {
        Config config = configRepository.getConfig(userLogin.getClientId());
        
        if (config == null) {
            return new ClientConfigDto();
        } else {
            return new ClientConfigDto(config);
        }
    }

    @Override
    public IdDto set(UserLogin userLogin, ClientConfigDto dto) {
        Config config = configRepository.getConfig(userLogin.getClientId());
        
        if (config == null) {
            config = new Config();
            initPOWhenCreate(Config.class, userLogin, config);
        }
        
        config.setVisitDistanceKPI(dto.getVisitDistanceKPI());
        config.setVisitDurationKPI(dto.getVisitDurationKPI());
        config.setCanEditCustomerLocation(dto.isCanEditCustomerLocation());
        config.setLocation(dto.getLocation());
        
        config = configRepository.save(userLogin.getClientId(), config);
        
        return new IdDto(config);
    }

}
