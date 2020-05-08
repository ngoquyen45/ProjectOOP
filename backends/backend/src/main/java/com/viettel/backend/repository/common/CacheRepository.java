package com.viettel.backend.repository.common;

import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import com.viettel.backend.domain.CalendarConfig;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Product;
import com.viettel.backend.engine.cache.CacheManager;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

public class CacheRepository {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CACHE_CONFIG = "CONFIG";
    private static final String CACHE_CALENDAR_CONFIG = "CALENDAR_CONFIG";
    private static final String CACHE_PRODUCT_MAP = "PRODUCT_MAP";
    private static final String CACHE_LIST_WORKING_DAYS = "LIST_WORKING_DAYS";
    private static final String CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK = "MAP_NBVISIT_PLAzNNED_BY_ROUTE_DAY_WEEK";

    private CacheManager cacheManager;

    public CacheRepository(CacheManager cacheManager) {
        super();
        this.cacheManager = cacheManager;
    }

    public void clearByClient(ObjectId clientId) {
        Assert.notNull(clientId);
        
        cacheManager.getValueCache(CACHE_CONFIG, String.class, Config.class).delete(clientId.toString());
        cacheManager.getValueCache(CACHE_PRODUCT_MAP, String.class, Map.class).delete(clientId.toString());
        cacheManager.getValueCache(CACHE_CALENDAR_CONFIG, String.class, CalendarConfig.class)
                .delete(clientId.toString());
        cacheManager.getHashCache(CACHE_LIST_WORKING_DAYS, String.class, Period.class, List.class)
                .delete(clientId.toString());
        cacheManager
                .getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class, Map.class)
                .delete(clientId.toString());
    }

    // CONFIG
    public Config getConfigCache(ObjectId clientId) {
        Assert.notNull(clientId);

        try {
            return cacheManager.getValueCache(CACHE_CONFIG, String.class, Config.class).get(clientId.toString()).get();
        } catch (Exception e) {
            logger.error("Cannot get Config from cache", e);
            return null;
        }
    }

    public void setConfigCache(ObjectId clientId, Config config) {
        Assert.notNull(clientId);
        Assert.notNull(config);

        try {
            cacheManager.getValueCache(CACHE_CONFIG, String.class, Config.class).get(clientId.toString()).set(config);
        } catch (Exception e) {
            logger.error("Cannot set Config to cache", e);
        }
    }

    // PRODUCT_MAP
    @SuppressWarnings("unchecked")
    public Map<ObjectId, Product> getProductMapCache(ObjectId clientId) {
        Assert.notNull(clientId);

        try {
            return cacheManager.getValueCache(CACHE_PRODUCT_MAP, String.class, Map.class).get(clientId.toString())
                    .get();
        } catch (Exception e) {
            logger.error("Cannot get productMap from cache", e);
            return null;
        }
    }

    public void setProductMapCache(ObjectId clientId, Map<ObjectId, Product> productMap) {
        Assert.notNull(clientId);

        try {
            cacheManager.getValueCache(CACHE_PRODUCT_MAP, String.class, Map.class).get(clientId.toString())
                    .set(productMap);
        } catch (Exception e) {
            logger.error("Cannot set productMap to cache", e);
        }
    }

    // CALENDAR CONFIG
    public CalendarConfig getCalendarConfigCache(ObjectId clientId) {
        Assert.notNull(clientId);

        try {
            return cacheManager.getValueCache(CACHE_CALENDAR_CONFIG, String.class, CalendarConfig.class)
                    .get(clientId.toString()).get();
        } catch (Exception e) {
            logger.error("Cannot get CalendarConfig from cache", e);
            return null;
        }
    }

    public void setCalendarConfigCache(ObjectId clientId, CalendarConfig calendarConfig) {
        Assert.notNull(clientId);
        Assert.notNull(calendarConfig);

        try {
            cacheManager.getValueCache(CACHE_CALENDAR_CONFIG, String.class, CalendarConfig.class)
                    .get(clientId.toString()).set(calendarConfig);
        } catch (Exception e) {
            logger.error("Cannot set CalendarConfig to cache", e);
        }
    }

    // LIST WORKING DAY
    @SuppressWarnings("unchecked")
    public List<SimpleDate> getWorkingDays(ObjectId clientId, Period period) {
        Assert.notNull(clientId);

        try {
            return cacheManager.getHashCache(CACHE_LIST_WORKING_DAYS, String.class, Period.class, List.class)
                    .get(clientId.toString()).get(period);
        } catch (Exception e) {
            logger.error("Cannot get list working days from cache", e);
            return null;
        }
    }

    public void setWorkingDays(ObjectId clientId, Period period, List<SimpleDate> workingDays) {
        Assert.notNull(clientId);

        try {
            cacheManager.getHashCache(CACHE_LIST_WORKING_DAYS, String.class, Period.class, List.class)
                    .get(clientId.toString()).put(period, workingDays);
        } catch (Exception e) {
            logger.error("Cannot set list working days to cache", e);
        }
    }

    public void clearWorkingDaysCache(ObjectId clientId) {
        Assert.notNull(clientId);

        try {
            cacheManager.getHashCache(CACHE_LIST_WORKING_DAYS, String.class, Period.class, List.class)
                    .delete(clientId.toString());
        } catch (Exception e) {
            logger.error("Cannot clear list working days to cache", e);
        }
    }

    // MAP NB VISIT PLANNED BY ROUTE DAY WEEK
    @SuppressWarnings("unchecked")
    public Map<ObjectId, Map<String, Integer>> getMapNbVisitPlannedByRouteDayWeek(ObjectId clientId,
            ObjectId distributorId) {
        Assert.notNull(clientId);
        Assert.notNull(distributorId);

        try {
            return cacheManager
                    .getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class, Map.class)
                    .get(clientId.toString()).get(distributorId.toString());
        } catch (Exception e) {
            logger.error("Cannot get map number visit planned by route cache", e);
            return null;
        }
    }

    public void setMapNbVisitPlannedByRouteDayWeek(ObjectId clientId, ObjectId distributorId,
            Map<ObjectId, Map<String, Integer>> map) {
        Assert.notNull(clientId);
        Assert.notNull(distributorId);

        try {
            cacheManager
                    .getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class, Map.class)
                    .get(clientId.toString()).put(distributorId.toString(), map);
        } catch (Exception e) {
            logger.error("Cannot set map number visit planned by route cache", e);
        }
    }

    public void clearMapNbVisitPlannedByRouteDayWeek(ObjectId clientId, ObjectId distributorId) {
        try {
            if (clientId != null) {
                if (distributorId != null) {
                    cacheManager.getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class,
                            Map.class).get(clientId.toString()).delete(distributorId.toString());
                } else {
                    cacheManager.getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class,
                            Map.class).delete(clientId.toString());
                }
            } else {
                cacheManager.getHashCache(CACHE_MAP_NBVISIT_PLANNED_BY_ROUTE_DAY_WEEK, String.class, String.class,
                        Map.class).clear();
            }

        } catch (Exception e) {
            logger.error("Cannot clear map number visit planned by route cache", e);
        }
    }

}
