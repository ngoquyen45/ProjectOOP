package com.viettel.backend.service.system.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.User.Role;
import com.viettel.backend.engine.cache.CacheManager;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.common.CacheService;
import com.viettel.backend.service.system.CacheInitiator;

@RolePermission(value = { Role.SUPER_ADMIN })
@Service
public class CacheInitiatorImpl implements CacheInitiator {

    @Autowired
    CacheManager cacheManager;
    
    @Autowired
    CacheService cacheService;

    @Override
    public void resetCache() {
        cacheManager.clearAll();
        cacheService.initBusinessCache(null);
    }
    
}
