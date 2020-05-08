package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Order;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.OrderPendingRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;

@Repository
public class OrderPendingRepositoryImpl extends AbstractRepository<Order>implements OrderPendingRepository {

    private static final Criteria DEFAULT_CRITERIA = Criteria.where(Order.COLUMNNAME_IS_ORDER).is(true);

    @Autowired
    private ConfigRepository configRepository;

    @Override
    protected Criteria getDefaultCriteria() {
        return DEFAULT_CRITERIA;
    }

    private Criteria getPendingCriteria(ObjectId clientId) {
        Config config = configRepository.getConfig(clientId);
        if (config == null) {
            throw new UnsupportedOperationException("systemConfig is null");
        }

        SimpleDate today = DateTimeUtils.getToday();

        long from = DateTimeUtils.addDays(today, -config.getNumberDayOrderPendingExpire()).getValue();
        long to = DateTimeUtils.getTomorrow().getValue();

        Criteria createdTimeCriteria = Criteria.where(Order.COLUMNNAME_CREATED_TIME_VALUE).gte(from).lt(to);
        Criteria pendingStatusCriteria = Criteria.where(Order.COLUMNNAME_APPROVE_STATUS)
                .is(Order.APPROVE_STATUS_PENDING);

        return CriteriaUtils.andOperator(createdTimeCriteria, pendingStatusCriteria);
    }

    @Override
    public List<Order> getPendingOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Pageable pageable, Sort sort) {
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria distributorCriteria = Criteria.where(Order.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria pendingCriteria = getPendingCriteria(clientId);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, pendingCriteria);

        return _getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countPendingOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return 0;
        }

        Criteria distributorCriteria = Criteria.where(Order.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria pendingCriteria = getPendingCriteria(clientId);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, pendingCriteria);

        return _count(clientId, false, true, criteria);
    }

    @Override
    public List<Order> getOrdersCreatedToday(ObjectId clientId, ObjectId createdUserId,
            Collection<ObjectId> customerIds, Sort sort) {
        Assert.notNull(createdUserId);

        Criteria createdByCriteria = Criteria.where(Order.COLUMNNAME_CREATED_BY_ID).is(createdUserId);
        Criteria todayPeriod = CriteriaUtils.getPeriodCriteria(Order.COLUMNNAME_CREATED_TIME_VALUE,
                DateTimeUtils.getPeriodToday());

        Criteria customerCriteria = null;
        if (customerIds != null) {
            customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        }

        Criteria criteria = CriteriaUtils.andOperator(createdByCriteria, todayPeriod, customerCriteria);

        return _getList(clientId, false, true, criteria, null, sort);
    }

    @Override
    public Order getById(ObjectId clientId, ObjectId id) {
        return super._getById(clientId, null, true, id);
    }

    @Override
    public Order save(ObjectId clientId, Order order) {
        return super._save(clientId, order);
    }
}
