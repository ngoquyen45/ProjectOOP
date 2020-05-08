package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Config.OrderDateType;
import com.viettel.backend.domain.Order;
import com.viettel.backend.repository.OrderRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class OrderRepositoryImpl extends BasicRepositoryImpl<Order> implements OrderRepository {

    private static final Criteria DEFAULT_CRITERIA = CriteriaUtils.andOperator(
            Criteria.where(Order.COLUMNNAME_IS_ORDER).is(true),
            Criteria.where(Order.COLUMNNAME_APPROVE_STATUS).is(Order.APPROVE_STATUS_APPROVED));

    @Override
    protected Criteria getDefaultCriteria() {
        return DEFAULT_CRITERIA;
    }

    @Override
    public List<Order> getOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            OrderDateType orderDateType, Pageable pageable, Sort sort) {
        Assert.notNull(distributorIds);
        Assert.notNull(period);
        Assert.notNull(orderDateType);

        if (distributorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria distributorCriteria = Criteria.where(Order.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria dateCriteria = getDateCriteria(period, orderDateType);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, dateCriteria);

        return super._getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countOrdersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            OrderDateType orderDateType) {
        Assert.notNull(distributorIds);
        Assert.notNull(period);
        Assert.notNull(orderDateType);

        if (distributorIds.isEmpty()) {
            return 0;
        }

        Criteria distributorCriteria = Criteria.where(Order.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria dateCriteria = getDateCriteria(period, orderDateType);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, dateCriteria);

        return super._count(clientId, false, true, criteria);
    }

    @Override
    public List<Order> getOrdersByCreatedUsers(ObjectId clientId, Collection<ObjectId> createdUserIds, Period period,
            OrderDateType orderDateType, Collection<ObjectId> customerIds) {
        Assert.notNull(createdUserIds);
        Assert.notNull(period);
        Assert.notNull(orderDateType);

        if (createdUserIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria createdByCriteria = Criteria.where(Order.COLUMNNAME_CREATED_BY_ID).in(createdUserIds);
        Criteria dateCriteria = getDateCriteria(period, orderDateType);

        Criteria customerCriteria = null;
        if (customerIds != null) {
            if (customerIds.isEmpty()) {
                return Collections.emptyList();
            }
            customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        }

        Criteria criteria = CriteriaUtils.andOperator(createdByCriteria, dateCriteria, customerCriteria);

        return super._getList(clientId, false, true, criteria, null, null);
    }

    @Override
    public List<Order> getOrdersByCustomers(ObjectId clientId, Collection<ObjectId> customerIds, Period period,
            OrderDateType orderDateType) {
        Assert.notNull(customerIds);
        Assert.notNull(period);
        Assert.notNull(orderDateType);

        if (customerIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        Criteria dateCriteria = getDateCriteria(period, orderDateType);

        Criteria criteria = CriteriaUtils.andOperator(customerCriteria, dateCriteria);

        return super._getList(clientId, false, true, criteria, null, null);
    }

    @Override
    public List<Order> getLastOrdersByCustomer(ObjectId clientId, ObjectId customerId, int size,
            OrderDateType orderDateType) {
        Assert.notNull(customerId);
        Assert.notNull(orderDateType);

        Criteria defaultDateCriteria = getDefaultDateCriteria(OrderDateType.CREATED_DATE);
        Criteria customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).is(customerId);
        
        Criteria criteria = CriteriaUtils.andOperator(defaultDateCriteria, customerCriteria);

        PageRequest pageable = new PageRequest(0, size);

        return super._getList(clientId, false, true, criteria, pageable,
                new Sort(Direction.DESC, getPeriodColumnName(orderDateType)));
    }

    @Override
    public List<Order> getOrdersByCreatedUsersOrCustomers(ObjectId clientId, Collection<ObjectId> createdUserIds,
            Collection<ObjectId> customerIds, String search, Pageable pageable) {
        Criteria defaultDateCriteria = getDefaultDateCriteria(OrderDateType.CREATED_DATE);

        Criteria createdByCriteria = null;
        if (createdUserIds != null) {
            createdByCriteria = Criteria.where(Order.COLUMNNAME_CREATED_BY_ID).in(createdUserIds);
        }

        Criteria customerCriteria = null;
        if (customerIds != null) {
            customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        }

        Criteria searchCriteria = null;
        if (search != null) {
            Criteria codeCriteria = CriteriaUtils.getSearchLikeCriteria(Order.COLUMNNAME_CODE, search);
            Criteria customerNameCriteria = CriteriaUtils.getSearchLikeCriteria(Order.COLUMNNAME_CUSTOMER_NAME, search);
            searchCriteria = CriteriaUtils.orOperator(codeCriteria, customerNameCriteria);
        }

        Criteria criteria = CriteriaUtils.andOperator(defaultDateCriteria,
                CriteriaUtils.orOperator(createdByCriteria, customerCriteria), searchCriteria);

        return super._getList(clientId, false, true, criteria, pageable,
                new Sort(Direction.DESC, getPeriodColumnName(OrderDateType.CREATED_DATE)));
    }

    @Override
    public long countOrdersByCreatedUsersOrCustomers(ObjectId clientId, Collection<ObjectId> createdUserIds,
            Collection<ObjectId> customerIds, String search) {
        Criteria defaultDateCriteria = getDefaultDateCriteria(OrderDateType.CREATED_DATE);

        Criteria createdByCriteria = null;
        if (createdUserIds != null) {
            createdByCriteria = Criteria.where(Order.COLUMNNAME_CREATED_BY_ID).in(createdUserIds);
        }

        Criteria customerCriteria = null;
        if (customerIds != null) {
            customerCriteria = Criteria.where(Order.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        }

        Criteria searchCriteria = null;
        if (search != null) {
            Criteria codeCriteria = CriteriaUtils.getSearchLikeCriteria(Order.COLUMNNAME_CODE, search);
            Criteria customerNameCriteria = CriteriaUtils.getSearchLikeCriteria(Order.COLUMNNAME_CUSTOMER_NAME, search);
            searchCriteria = CriteriaUtils.orOperator(codeCriteria, customerNameCriteria);
        }

        Criteria criteria = CriteriaUtils.andOperator(defaultDateCriteria,
                CriteriaUtils.orOperator(createdByCriteria, customerCriteria), searchCriteria);
        
        return super._count(clientId, false, true, criteria);
    }

    // PRIVATE
    private Criteria getDateCriteria(Period period, OrderDateType orderDateType) {
        return CriteriaUtils.getPeriodCriteria(getPeriodColumnName(orderDateType), period);
    }

    private String getPeriodColumnName(OrderDateType orderDateType) {
        if (orderDateType == OrderDateType.CREATED_DATE) {
            return Order.COLUMNNAME_CREATED_TIME_VALUE;
        } else {
            return Order.COLUMNNAME_APPROVE_TIME_VALUE;
        }
    }

    // LAST 3 MONTH
    private Criteria getDefaultDateCriteria(OrderDateType orderDateType) {
        SimpleDate today = DateTimeUtils.getToday();
        return Criteria.where(getPeriodColumnName(orderDateType)).gte(DateTimeUtils.addMonths(today, -3).getValue());
    }

}
