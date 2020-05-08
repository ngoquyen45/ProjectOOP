package com.viettel.backend.service.common;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Client;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Data;
import com.viettel.backend.domain.Order;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.OrderDetail;
import com.viettel.backend.engine.cache.CacheManager;
import com.viettel.backend.repository.ClientRepository;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.DataRepository;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;

@Service
public class CacheService extends AbstractService {

    public static enum DateType {
        DAILY, MONTHLY
    }

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private ConfigRepository configRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String CACHE_KEY = "KEY_USED";

    private static final String CACHE_REVENUE = "REVENUE";
    private static final String CACHE_PRODUCTIVITY = "PRODUCTIVITY";
    private static final String CACHE_NBORDER = "NBORDER";
    private static final String CACHE_NBORDER_NOVISIT = "NBORDER_NOVISIT";
    private static final String CACHE_QUANTITY = "QUANTITY";

    private static final String CACHE_NBVISIT = "NBVISIT";
    private static final String CACHE_NBVISIT_WITH_ORDER = "CACHE_NBVISIT_WITH_ORDER";
    private static final String CACHE_NBVISIT_ERROR_DURATION = "NBVISIT_ERROR_DURATION";
    private static final String CACHE_NBVISIT_ERROR_POSITION = "NBVISIT_ERROR_POSITION";

    private static final String THIRD_KEY_CUSTOMER = "CUSTOMER";
    private static final String THIRD_KEY_SALESMAN = "SALESMAN";
    private static final String THIRD_KEY_CREATED = "CREATED";
    private static final String THIRD_KEY_PRODUCT = "PRODUCT";

    //
    //
    //
    //
    //
    //

    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ***********************ADD**********************************
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void clearByClient(ObjectId clientId) {
        Map<String, Set> keyMap = this.cacheManager.getHashCache(CACHE_KEY, String.class, String.class, Set.class)
                .get(clientId.toString()).entries();
        if (keyMap != null) {
            for (Entry<String, Set> entry : keyMap.entrySet()) {
                this.cacheManager.getHashCache(entry.getKey(), String.class, ObjectId.class, Double.class)
                        .delete(entry.getValue());
            }
        }
        this.cacheManager.getHashCache(CACHE_KEY, String.class, String.class, Set.class).delete(clientId.toString());
    }

    public void initBusinessCache(ObjectId clientId) {
        // GET CONFIG MAP
        HashMap<ObjectId, Config> configByClient = new HashMap<>();
        if (clientId == null) {
            List<Client> clients = clientRepository.getAll(PO.CLIENT_ROOT_ID, null);
            for (Client client : clients) {
                configByClient.put(client.getId(), configRepository.getConfig(client.getId()));
            }
        } else {
            configByClient.put(clientId, configRepository.getConfig(clientId));
        }

        int recordByPage = 5000;
        long totalRecord = dataRepository.countDatas(clientId, DateTimeUtils.getFirstOfLastMonth());
        LocalCache localCache = new LocalCache();
        long nbPaae = totalRecord / recordByPage + (totalRecord % recordByPage > 0 ? 1 : 0);

        for (int page = 0; page < nbPaae; page++) {
            List<Data> datas = dataRepository.getDatas(clientId, DateTimeUtils.getFirstOfLastMonth(),
                    new PageRequest(page, recordByPage));

            if (datas != null) {
                for (Data data : datas) {
                    if (data.isOrder() && data.getApproveStatus() == Order.APPROVE_STATUS_APPROVED) {
                        _addNewApprovedOrder(localCache, configByClient.get(data.getClientId()), data);
                    }

                    if (data.isVisit() && data.getVisitStatus() == Visit.VISIT_STATUS_VISITED) {
                        _addNewVisited(localCache, data);
                    }
                }
            }
        }

        localCache.applyToCacheManager(this.cacheManager);
    }

    public void addNewApprovedOrder(Order order) {
        Config config = configRepository.getConfig(order.getClientId());
        _addNewApprovedOrder(null, config, order);
    }

    private void _addNewApprovedOrder(LocalCache localCache, Config config, Order order) {
        Assert.notNull(order);
        Assert.notNull(order.getDistributor());
        Assert.notNull(order.getCreatedBy());
        Assert.notNull(order.getCustomer());
        Assert.notNull(order.getCreatedTime());
        Assert.notNull(order.getApproveTime());

        ObjectId distributorId = order.getDistributor().getId();
        ObjectId createdId = order.getCreatedBy().getId();
        ObjectId customerId = order.getCustomer().getId();

        SimpleDate date = (config.getOrderDateType() == OrderDateType.CREATED_DATE) ? order.getCreatedTime()
                : order.getApproveTime();

        // REVENUE
        double revenue = order.getGrandTotal().doubleValue();
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.DAILY, distributorId, null, null,
                revenue);
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.MONTHLY, distributorId, null, null,
                revenue);
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CREATED, createdId, revenue);
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CREATED, createdId, revenue);
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, revenue);
        increment(localCache, CACHE_REVENUE, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, revenue);

        // PRODUCTTIVITY
        double productivity = order.getProductivity().doubleValue();
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.DAILY, distributorId, null, null,
                productivity);
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.MONTHLY, distributorId, null,
                null, productivity);
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CREATED, createdId, productivity);
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CREATED, createdId, productivity);
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, productivity);
        increment(localCache, CACHE_PRODUCTIVITY, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, productivity);

        // NBORDER
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.DAILY, distributorId, null, null, 1);
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.MONTHLY, distributorId, null, null, 1);
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CREATED, createdId, 1);
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CREATED, createdId, 1);
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, 1);
        increment(localCache, CACHE_NBORDER, order.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_CUSTOMER, customerId, 1);

        // NBORDER NOVISIT
        if (!order.isVisit()) {
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.DAILY, distributorId, null,
                    null, 1);
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.MONTHLY, distributorId,
                    null, null, 1);
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.DAILY, distributorId,
                    THIRD_KEY_CREATED, createdId, 1);
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.MONTHLY, distributorId,
                    THIRD_KEY_CREATED, createdId, 1);
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.DAILY, distributorId,
                    THIRD_KEY_CUSTOMER, customerId, 1);
            increment(localCache, CACHE_NBORDER_NOVISIT, order.getClientId(), date, DateType.MONTHLY, distributorId,
                    THIRD_KEY_CUSTOMER, customerId, 1);
        }

        // QUANTITY
        for (OrderDetail orderDetail : order.getDetails()) {
            ObjectId productId = orderDetail.getProduct().getId();
            double quantity = orderDetail.getQuantity().doubleValue();

            increment(localCache, CACHE_QUANTITY, order.getClientId(), date, DateType.DAILY, distributorId,
                    THIRD_KEY_PRODUCT, productId, quantity);
            increment(localCache, CACHE_QUANTITY, order.getClientId(), date, DateType.MONTHLY, distributorId,
                    THIRD_KEY_PRODUCT, productId, quantity);
        }
    }

    public void addNewVisited(Visit visit) {
        _addNewVisited(null, visit);
    }

    private void _addNewVisited(LocalCache localCache, Visit visit) {
        Assert.notNull(visit);
        Assert.notNull(visit.getDistributor());
        Assert.notNull(visit.getSalesman());

        ObjectId distributorId = visit.getDistributor().getId();
        ObjectId salesmanId = visit.getCreatedBy().getId();

        SimpleDate date = visit.getStartTime();

        // NB VISIT
        increment(localCache, CACHE_NBVISIT, visit.getClientId(), date, DateType.DAILY, distributorId, null, null, 1);
        increment(localCache, CACHE_NBVISIT, visit.getClientId(), date, DateType.MONTHLY, distributorId, null, null, 1);
        increment(localCache, CACHE_NBVISIT, visit.getClientId(), date, DateType.DAILY, distributorId,
                THIRD_KEY_SALESMAN, salesmanId, 1);
        increment(localCache, CACHE_NBVISIT, visit.getClientId(), date, DateType.MONTHLY, distributorId,
                THIRD_KEY_SALESMAN, salesmanId, 1);

        if (visit.isOrder()) {
            increment(localCache, CACHE_NBVISIT_WITH_ORDER, visit.getClientId(), date, DateType.DAILY, distributorId,
                    null, null, 1);
            increment(localCache, CACHE_NBVISIT_WITH_ORDER, visit.getClientId(), date, DateType.MONTHLY, distributorId,
                    null, null, 1);
            increment(localCache, CACHE_NBVISIT_WITH_ORDER, visit.getClientId(), date, DateType.DAILY, distributorId,
                    THIRD_KEY_SALESMAN, salesmanId, 1);
            increment(localCache, CACHE_NBVISIT_WITH_ORDER, visit.getClientId(), date, DateType.MONTHLY, distributorId,
                    THIRD_KEY_SALESMAN, salesmanId, 1);
        }

        if (visit.isErrorDuration()) {
            increment(localCache, CACHE_NBVISIT_ERROR_DURATION, visit.getClientId(), date, DateType.DAILY,
                    distributorId, null, null, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_DURATION, visit.getClientId(), date, DateType.MONTHLY,
                    distributorId, null, null, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_DURATION, visit.getClientId(), date, DateType.DAILY,
                    distributorId, THIRD_KEY_SALESMAN, salesmanId, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_DURATION, visit.getClientId(), date, DateType.MONTHLY,
                    distributorId, THIRD_KEY_SALESMAN, salesmanId, 1);
        }

        if (visit.getLocationStatus() != Visit.LOCATION_STATUS_LOCATED) {
            increment(localCache, CACHE_NBVISIT_ERROR_POSITION, visit.getClientId(), date, DateType.DAILY,
                    distributorId, null, null, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_POSITION, visit.getClientId(), date, DateType.MONTHLY,
                    distributorId, null, null, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_POSITION, visit.getClientId(), date, DateType.DAILY,
                    distributorId, THIRD_KEY_SALESMAN, salesmanId, 1);
            increment(localCache, CACHE_NBVISIT_ERROR_POSITION, visit.getClientId(), date, DateType.MONTHLY,
                    distributorId, THIRD_KEY_SALESMAN, salesmanId, 1);
        }
    }

    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************

    //
    //
    //
    //
    //

    // ************************************************************
    // ************************************************************
    // *************************GETTER*****************************
    // ************************************************************
    // ************************************************************
    // DISTRIBUTOR
    public double getRevenueByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_REVENUE, clientId, date, dateType, distributorIds);
    }

    public double getProductivityByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_PRODUCTIVITY, clientId, date, dateType, distributorIds);
    }

    public double getNbOrderByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBORDER, clientId, date, dateType, distributorIds);
    }

    public double getNbOrderNoVisitByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBORDER_NOVISIT, clientId, date, dateType, distributorIds);
    }

    public double getNbVisitByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBVISIT, clientId, date, dateType, distributorIds);
    }

    public double getNbVisitWithOrderByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBVISIT_WITH_ORDER, clientId, date, dateType, distributorIds);
    }

    public double getNbVisitErrorDurationByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_DURATION, clientId, date, dateType, distributorIds);
    }

    public double getNbVisitErrorPositionByDistributor(ObjectId clientId, DateType dateType, SimpleDate date,
            Collection<ObjectId> distributorIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_POSITION, clientId, date, dateType, distributorIds);
    }

    // CREATED BY
    public double getRevenueByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_REVENUE, clientId, date, dateType, distributorId, THIRD_KEY_CREATED, createdIds);
    }

    public double getProductivityByCreated(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId, Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_PRODUCTIVITY, clientId, date, dateType, distributorId, THIRD_KEY_CREATED, createdIds);
    }

    public double getNbOrderByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBORDER, clientId, date, dateType, distributorId, THIRD_KEY_CREATED, createdIds);
    }

    public double getNbOrderNoVisitByCreated(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId, Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBORDER_NOVISIT, clientId, date, dateType, distributorId, THIRD_KEY_CREATED,
                createdIds);
    }
    
    public double getNbVisitByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBVISIT, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN, createdIds);
    }

    public double getNbVisitWithOrderByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBVISIT_WITH_ORDER, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN, createdIds);
    }

    public double getNbVisitErrorDurationByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_DURATION, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN, createdIds);
    }

    public double getNbVisitErrorPositionByCreated(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> createdIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_POSITION, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN, createdIds);
    }

    // CUSTOMER
    public Map<ObjectId, Double> getRevenueByCustomer(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_REVENUE, clientId, date, dateType, distributorId, THIRD_KEY_CUSTOMER);
    }

    public Map<ObjectId, Double> getProductivityByCustomer(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_PRODUCTIVITY, clientId, date, dateType, distributorId, THIRD_KEY_CUSTOMER);
    }

    public Map<ObjectId, Double> getNbOrderByCustomer(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_NBORDER, clientId, date, dateType, distributorId, THIRD_KEY_CUSTOMER);
    }

    public Map<ObjectId, Double> getNbOrderNoVisitByCustomer(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_NBORDER_NOVISIT, clientId, date, dateType, distributorId, THIRD_KEY_CUSTOMER);
    }

    // QUANTITY
    public Map<ObjectId, Double> getQuantityProductMap(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_QUANTITY, clientId, date, dateType, distributorId, THIRD_KEY_PRODUCT);
    }

    // SALESMAN
    public double getNbVisitBySalesman(ObjectId clientId, DateType dateType, SimpleDate date, ObjectId distributorId,
            Collection<ObjectId> salesmanIds) {
        return getValueSum(CACHE_NBVISIT, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN, salesmanIds);
    }

    public Map<ObjectId, Double> getNbVisitBySalesmanMap(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId) {
        return getMapValue(CACHE_NBVISIT, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN);
    }

    public double getNbVisitWithOrderBySalesman(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId, Collection<ObjectId> salesmanIds) {
        return getValueSum(CACHE_NBVISIT_WITH_ORDER, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN,
                salesmanIds);
    }

    public double getNbVisitErrorDurationBySalesman(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId, Collection<ObjectId> salesmanIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_DURATION, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN,
                salesmanIds);
    }

    public double getNbVisitErrorPositionBySalesman(ObjectId clientId, DateType dateType, SimpleDate date,
            ObjectId distributorId, Collection<ObjectId> salesmanIds) {
        return getValueSum(CACHE_NBVISIT_ERROR_POSITION, clientId, date, dateType, distributorId, THIRD_KEY_SALESMAN,
                salesmanIds);
    }

    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************

    //
    //
    //
    //
    //

    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ************************************************************
    // ***********************PRIVATE******************************

    private void increment(LocalCache localCache, String cacheName, ObjectId clientId, SimpleDate date,
            DateType dateType, ObjectId distributorId, String thirdKeyName, ObjectId thirdKeyId, double value) {
        Assert.notNull(clientId);
        Assert.notNull(date);
        Assert.notNull(distributorId);
        Assert.isTrue((thirdKeyName == null && thirdKeyId == null) || (thirdKeyName != null && thirdKeyId != null));

        if (dateType == DateType.DAILY && date.compareTo(DateTimeUtils.getFirstOfThisMonth()) < 0) {
            // DO NOT AND DAILY CACHE IF IS NOT CURRENT MONTH
            return;
        }

        String dateKey = null;
        if (dateType == DateType.DAILY) {
            dateKey = date.getIsoDate();
        } else {
            dateKey = date.getIsoMonth();
        }

        String firstKey = clientId.toString() + "@" + dateKey;
        ObjectId secondKey = distributorId;
        if (thirdKeyName != null && thirdKeyId != null) {
            firstKey = firstKey + '@' + distributorId.toString() + '@' + thirdKeyName;
            secondKey = thirdKeyId;
        }
        if (localCache != null) {
            localCache.increment(cacheName, clientId, firstKey, secondKey, value);
        } else {
            try {
                this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class).get(firstKey)
                        .increment(secondKey, value);

                this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class).get(firstKey)
                        .expire(63, TimeUnit.DAYS);

                @SuppressWarnings("unchecked")
                Set<String> keys = this.cacheManager.getHashCache(CACHE_KEY, String.class, String.class, Set.class)
                        .get(clientId.toString()).get(cacheName);
                if (keys == null) {
                    keys = new HashSet<>();
                }
                keys.add(firstKey);
                this.cacheManager.getHashCache(CACHE_KEY, String.class, String.class, Set.class)
                        .get(clientId.toString()).put(cacheName, keys);

            } catch (Exception e) {
                logger.error("cache error when increment", e);
            }
        }
    }

    @SuppressWarnings("unused")
    private double getValue(String cacheName, ObjectId clientId, SimpleDate date, DateType dateType,
            ObjectId distributorId, String thirdKeyName, ObjectId thirdKeyId) {
        Assert.notNull(date);
        Assert.notNull(distributorId);
        Assert.isTrue((thirdKeyName == null && thirdKeyId == null) || (thirdKeyName != null && thirdKeyId != null));

        String dateKey = null;
        if (dateType == DateType.DAILY) {
            dateKey = date.getIsoDate();
        } else {
            dateKey = date.getIsoMonth();
        }

        String firstKey = clientId.toString() + "@" + dateKey;
        ObjectId secondKey = distributorId;
        if (thirdKeyName != null && thirdKeyId != null) {
            firstKey = firstKey + '@' + distributorId.toString() + '@' + thirdKeyName;
            secondKey = thirdKeyId;
        }

        try {
            Double value = this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class)
                    .get(firstKey).get(secondKey);

            return value == null ? 0 : value;
        } catch (Exception e) {
            logger.error("cache error when get value", e);
            return 0;
        }
    }

    private double getValueSum(String cacheName, ObjectId clientId, SimpleDate date, DateType dateType,
            Collection<ObjectId> distributorIds) {
        Assert.notNull(date);
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return 0;
        }

        String dateKey = null;
        if (dateType == DateType.DAILY) {
            dateKey = date.getIsoDate();
        } else {
            dateKey = date.getIsoMonth();
        }

        String firstKey = clientId.toString() + "@" + dateKey;

        List<Double> values = this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class)
                .get(firstKey).multiGet(distributorIds);

        if (values == null || values.isEmpty()) {
            return 0;
        } else {
            double value = 0;
            for (Double _value : values) {
                value += _value == null ? 0 : _value.doubleValue();
            }

            return value;
        }
    }

    private double getValueSum(String cacheName, ObjectId clientId, SimpleDate date, DateType dateType,
            ObjectId distributorId, String thirdKeyName, Collection<ObjectId> thirdKeyIds) {
        Assert.notNull(date);
        Assert.notNull(date);
        Assert.notNull(distributorId);
        Assert.isTrue(thirdKeyName != null && thirdKeyIds != null);

        if (thirdKeyIds.isEmpty()) {
            return 0;
        }

        String dateKey = null;
        if (dateType == DateType.DAILY) {
            dateKey = date.getIsoDate();
        } else {
            dateKey = date.getIsoMonth();
        }

        String firstKey = clientId.toString() + "@" + dateKey + '@' + distributorId.toString() + '@' + thirdKeyName;

        try {
            List<Double> values = this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class)
                    .get(firstKey).multiGet(thirdKeyIds);

            if (values == null || values.isEmpty()) {
                return 0;
            } else {
                double value = 0;
                for (Double _value : values) {
                    value += _value == null ? 0 : _value.doubleValue();
                }

                return value;
            }
        } catch (Exception e) {
            logger.error("cache error when get value", e);
            return 0;
        }
    }

    private Map<ObjectId, Double> getMapValue(String cacheName, ObjectId clientId, SimpleDate date, DateType dateType,
            ObjectId distributorId, String thirdKeyName) {
        Assert.notNull(date);
        Assert.notNull(distributorId);

        String dateKey = null;
        if (dateType == DateType.DAILY) {
            dateKey = date.getIsoDate();
        } else {
            dateKey = date.getIsoMonth();
        }

        String firstKey = clientId.toString() + "@" + dateKey;
        if (thirdKeyName != null) {
            firstKey = firstKey + '@' + distributorId.toString() + '@' + thirdKeyName;
        }

        try {
            return this.cacheManager.getHashCache(cacheName, String.class, ObjectId.class, Double.class).get(firstKey)
                    .entries();
        } catch (Exception e) {
            logger.error("cache error when get value", e);
            return Collections.emptyMap();
        }
    }

    private static class LocalCache extends HashMap<String, Map<String, Map<ObjectId, Double>>> {

        private static final long serialVersionUID = -1407667477206125091L;

        private Logger logger = LoggerFactory.getLogger(this.getClass());

        private HashMap<ObjectId, Map<String, Set<String>>> keyMapByClient;

        private LocalCache() {
            super();
            this.keyMapByClient = new HashMap<>();
        }

        protected void increment(String cacheName, ObjectId clientId, String firstKey, ObjectId secondKey,
                double _value) {
            Map<String, Map<ObjectId, Double>> map1 = this.get(cacheName);
            if (map1 == null) {
                map1 = new HashMap<>();
                this.put(cacheName, map1);
            }

            Map<ObjectId, Double> map2 = map1.get(firstKey);
            if (map2 == null) {
                map2 = new HashMap<>();
                map1.put(firstKey, map2);
            }

            Double value = map2.get(secondKey);
            if (value == null) {
                value = 0.0;
            }
            map2.put(secondKey, value + _value);

            Map<String, Set<String>> keyMap = this.keyMapByClient.get(clientId);
            Set<String> keys = null;
            if (keyMap == null) {
                keyMap = new HashMap<>();
                this.keyMapByClient.put(clientId, keyMap);
            }

            keys = keyMap.get(cacheName);
            if (keys == null) {
                keys = new HashSet<>();
                keyMap.put(cacheName, keys);
                this.keyMapByClient.put(clientId, keyMap);
            }

            keys.add(firstKey);
        }

        protected void applyToCacheManager(CacheManager cacheManager) {
            try {
                for (Map.Entry<String, Map<String, Map<ObjectId, Double>>> entry1 : this.entrySet()) {
                    for (Map.Entry<String, Map<ObjectId, Double>> entry2 : entry1.getValue().entrySet()) {
                        cacheManager.getHashCache(entry1.getKey(), String.class, ObjectId.class, Double.class)
                                .get(entry2.getKey()).putAll(entry2.getValue());
                        cacheManager.getHashCache(entry1.getKey(), String.class, ObjectId.class, Double.class)
                                .get(entry2.getKey()).expire(63, TimeUnit.DAYS);
                    }
                }

                for (Map.Entry<ObjectId, Map<String, Set<String>>> keyMapEntry : this.getKeyMapByClient().entrySet()) {
                    for (Map.Entry<String, Set<String>> keysEntry : keyMapEntry.getValue().entrySet()) {
                        cacheManager.getHashCache(CACHE_KEY, String.class, String.class, Set.class)
                                .get(keyMapEntry.getKey().toString()).put(keysEntry.getKey(), keysEntry.getValue());
                    }
                }

            } catch (Exception e) {
                logger.error("cache error when increment", e);
            }
        }

        public HashMap<ObjectId, Map<String, Set<String>>> getKeyMapByClient() {
            return keyMapByClient;
        }

    }

}
