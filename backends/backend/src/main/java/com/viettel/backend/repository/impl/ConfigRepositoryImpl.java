package com.viettel.backend.repository.impl;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.PO;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.common.CacheRepository;

@Repository
public class ConfigRepositoryImpl extends AbstractRepository<Config>implements ConfigRepository {

    @Autowired
    private CacheRepository cacheRepository;

    @Override
    public Config getConfig(ObjectId clientId) {
        Config config = cacheRepository.getConfigCache(clientId);
        if (config == null) {
            config = _getFirst(clientId, false, true, null, null);
            if (config != null) {
                cacheRepository.setConfigCache(clientId, config);
            }
        }

        if (!clientId.equals(PO.CLIENT_ROOT_ID)) {
            Config rootConfig = getConfig(PO.CLIENT_ROOT_ID);

            if (config == null) {
                config = new Config();
                config.setActive(true);
                config.setDraft(false);
                config.setClientId(clientId);
            }

            // DEFAULT
            config.setDateFormat(rootConfig.getDateFormat());
            config.setProductPhoto(rootConfig.getProductPhoto());

            // CALENDAR
            config.setFirstDayOfWeek(rootConfig.getFirstDayOfWeek());
            config.setMinimalDaysInFirstWeek(rootConfig.getMinimalDaysInFirstWeek());
            
            // SCHEDULE
            config.setComplexSchedule(rootConfig.isComplexSchedule());
            config.setNumberWeekOfFrequency(rootConfig.getNumberWeekOfFrequency());
            
            // NUMBER DAY ORDER PENDING EXPIRE
            config.setNumberDayOrderPendingExpire(rootConfig.getNumberDayOrderPendingExpire());
            // ORDER DATE TYPE
            config.setOrderDateType(rootConfig.getOrderDateType());

            return config;
        } else {
            return config;
        }
    }

    @Override
    public Config save(ObjectId clientId, Config config) {
        Assert.notNull(config);

        config = _save(clientId, config);
        cacheRepository.setConfigCache(clientId, config);

        return config;
    }

}
