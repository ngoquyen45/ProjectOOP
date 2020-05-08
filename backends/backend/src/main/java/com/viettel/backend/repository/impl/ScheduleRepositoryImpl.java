package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.repository.CalendarConfigRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.RouteRepository;
import com.viettel.backend.repository.ScheduleRepository;
import com.viettel.backend.repository.common.CacheRepository;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class ScheduleRepositoryImpl implements ScheduleRepository {

    @Autowired
    private CacheRepository cacheRepository;

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Autowired
    private ConfigRepository configRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RouteRepository routeRepository;

    @Override
    public void notifyScheduleChange(ObjectId clientId, ObjectId distributorId) {
        cacheRepository.clearMapNbVisitPlannedByRouteDayWeek(clientId, distributorId);
    }

    @Override
    public Map<ObjectId, Integer> getNbVisitPlannedByRoute(ObjectId clientId, Collection<ObjectId> distributorIds, Period period) {
        Assert.notNull(clientId);
        Assert.notNull(distributorIds);
        Assert.notNull(period);
        
        if (distributorIds.isEmpty()) {
            Collections.emptyMap();
        }

        Map<ObjectId, Map<String, Integer>> nbVisitPlannedByRouteDayWeek = new HashMap<>();
        for (ObjectId distributorId : distributorIds) {
            nbVisitPlannedByRouteDayWeek.putAll(getMapNbVisitPlannedByRouteDayWeek(clientId, distributorId));
        }

        if (nbVisitPlannedByRouteDayWeek.isEmpty()) {
            Collections.emptyMap();
        }

        // **********
        // GET MAP DAY
        HashMap<String, Integer> mapNbDayByDayWeekIndex = new HashMap<String, Integer>();

        List<SimpleDate> workingDays = calendarConfigRepository.getWorkingDays(clientId, period);
        Config config = configRepository.getConfig(clientId);
        if (config == null) {
            throw new UnsupportedOperationException("systemConfig not found");
        }

        if (workingDays.isEmpty()) {
            return Collections.emptyMap();
        }

        for (SimpleDate date : workingDays) {
            String key = date.getDayOfWeek() + "_" + config.getWeekIndex(date);

            Integer value = mapNbDayByDayWeekIndex.get(key);
            value = (value == null ? 0 : value) + 1;
            mapNbDayByDayWeekIndex.put(key, value);
        }
        // **********
        // **********

        Map<ObjectId, Integer> map = new HashMap<ObjectId, Integer>();
        for (ObjectId routeId : nbVisitPlannedByRouteDayWeek.keySet()) {
            int nbVisitPlanned = 0;

            for (Entry<String, Integer> entry : mapNbDayByDayWeekIndex.entrySet()) {
                Map<String, Integer> tmpMap = nbVisitPlannedByRouteDayWeek.get(routeId);
                if (tmpMap == null) {
                    break;
                }

                Integer value = tmpMap.get(entry.getKey());
                if (value != null) {
                    nbVisitPlanned = nbVisitPlanned + (value * entry.getValue());
                }
            }

            map.put(routeId, nbVisitPlanned);
        }

        return map;
    }

    // PRIVATE
    /**
     * 
     * @return Map<RouteId, Map<day_week, nbVisit>
     */
    private Map<ObjectId, Map<String, Integer>> getMapNbVisitPlannedByRouteDayWeek(ObjectId clientId,
            ObjectId distributorId) {
        Map<ObjectId, Map<String, Integer>> map1 = cacheRepository
                .getMapNbVisitPlannedByRouteDayWeek(clientId, distributorId);
        if (map1 != null) {
            // FOUND IT IN CACHE
            return map1;
        }

        map1 = new HashMap<ObjectId, Map<String, Integer>>();

        // GET ALL ROUTES OF THIS DISTRIBUTOR
        List<Route> routes = routeRepository.getAll(clientId, distributorId);
        if (routes.isEmpty()) {
            cacheRepository.setMapNbVisitPlannedByRouteDayWeek(clientId, distributorId, map1);
            return map1;
        }
        Set<ObjectId> routeIds = new HashSet<ObjectId>();
        for (Route route : routes) {
            routeIds.add(route.getId());
        }

        // GET ALL CUSTOMER
        List<Customer> customers = customerRepository.getCustomersByRoutes(clientId, routeIds, null);
        Config config = configRepository.getConfig(clientId);
        for (Customer customer : customers) {

            if (customer.getSchedule() != null && customer.getSchedule().getRouteId() != null
                    && customer.getSchedule().getItems() != null && !customer.getSchedule().getItems().isEmpty()) {

                for (ScheduleItem item : customer.getSchedule().getItems()) {

                    List<Integer> weeks = null;
                    if (config.getNumberWeekOfFrequency() > 1) {
                        weeks = item.getWeeks();
                    } else {
                        weeks = Collections.singletonList(0);
                    }

                    if (weeks != null) {
                        for (int week : weeks) {
                            for (int day : item.getDays()) {
                                String key = day + "_" + week;

                                Map<String, Integer> map2 = map1.get(customer.getSchedule().getRouteId());
                                if (map2 == null) {
                                    map2 = new HashMap<String, Integer>();
                                    map1.put(customer.getSchedule().getRouteId(), map2);
                                }

                                Integer value = (Integer) map2.get(key);
                                value = (value == null ? 0 : value) + 1;
                                map2.put(key, value);
                            }
                        }
                    }
                }
            }
        }

        cacheRepository.setMapNbVisitPlannedByRouteDayWeek(clientId, distributorId, map1);
        return map1;
    }

}
