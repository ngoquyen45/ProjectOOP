package com.viettel.backend.service.config.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.config.SystemConfigDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.AbstractService;
import com.viettel.backend.service.config.SystemConfigService;

@RolePermission(value = { Role.SUPER_ADMIN })
@Service
public class SystemConfigServiceImpl extends AbstractService implements SystemConfigService {

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public SystemConfigDto get(UserLogin userLogin) {
        Config config = configRepository.getConfig(PO.CLIENT_ROOT_ID);

        if (config == null) {
            return new SystemConfigDto();
        } else {
            return new SystemConfigDto(config);
        }
    }

    @Override
    public IdDto set(UserLogin userLogin, SystemConfigDto dto) {
        Config config = configRepository.getConfig(PO.CLIENT_ROOT_ID);

        if (config == null) {
            config = new Config();
            initPOWhenCreate(Config.class, userLogin, config);
        }

        config.setProductPhoto(dto.getProductPhoto());
        config.setDateFormat(dto.getDateFormat());

        config.setFirstDayOfWeek(dto.getFirstDayOfWeek());
        config.setMinimalDaysInFirstWeek(dto.getMinimalDaysInFirstWeek());
        
        config.setComplexSchedule(dto.isComplexSchedule());
        config.setNumberWeekOfFrequency(dto.getNumberWeekOfFrequency());
        
        config.setOrderDateType(dto.getOrderDateType());
        config.setNumberDayOrderPendingExpire(dto.getNumberDayOrderPendingExpire());

        config.setLocation(dto.getLocation());
        
        config = configRepository.save(PO.CLIENT_ROOT_ID, config);

        scheduleRepository.notifyScheduleChange(null, null);

        return new IdDto(config);
    }

}
