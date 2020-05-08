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

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.Config;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.embed.ScheduleItem;
import com.viettel.backend.repository.ConfigRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.entity.SimpleDate;

@Repository
public class CustomerRepositoryImpl extends CategoryRepositoryImpl<Customer> implements CustomerRepository {

    private static final Criteria DEFAULT_CRITERIA = Criteria.where(Customer.COLUMNNAME_APPROVE_STATUS).is(
            Customer.APPROVE_STATUS_APPROVED);

    @Autowired
    private ConfigRepository configRepository;

    @Override
    protected Criteria getDefaultCriteria() {
        return DEFAULT_CRITERIA;
    }

    @Override
    public List<Customer> getCustomers(ObjectId clientId, ObjectId distributorId, boolean searchByRoute,
            ObjectId routeId, Integer dayOfWeek, String search, Pageable pageable, Sort sort) {
        Assert.notNull(distributorId);
        Criteria distributorCriteria = Criteria.where(Customer.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);

        Criteria scheduleNullCriteria = null;
        Criteria routeCriteria = null;
        if (searchByRoute) {
            if (routeId == null) {
                scheduleNullCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE).is(null);
            } else {
                routeCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ROUTE_ID).is(routeId);
            }
        }

        Criteria dayOfWeekCriteria = null;
        if (scheduleNullCriteria == null && dayOfWeek != null) {
            Config config = configRepository.getConfig(clientId);

            if (config.isComplexSchedule()) {
                dayOfWeekCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ITEMS).elemMatch(
                        Criteria.where(ScheduleItem.getDayColumnname(dayOfWeek)).is(true));
            } else {
                dayOfWeekCriteria = Criteria.where(
                        Customer.COLUMNNAME_SCHEDULE_ITEM + "." + ScheduleItem.getDayColumnname(dayOfWeek)).is(true);
            }
        }

        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, scheduleNullCriteria, routeCriteria,
                dayOfWeekCriteria, searchCriteria);

        return _getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countCustomers(ObjectId clientId, ObjectId distributorId, boolean searchByRoute, ObjectId routeId,
            Integer dayOfWeek, String search) {
        Assert.notNull(distributorId);
        Criteria distributorCriteria = Criteria.where(Customer.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);

        Criteria scheduleNullCriteria = null;
        Criteria routeCriteria = null;
        if (searchByRoute) {
            if (routeId == null) {
                scheduleNullCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE).is(null);
            } else {
                routeCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ROUTE_ID).is(routeId);
            }
        }

        Criteria dayOfWeekCriteria = null;
        if (scheduleNullCriteria == null && dayOfWeek != null) {
            Config config = configRepository.getConfig(clientId);

            if (config.isComplexSchedule()) {
                dayOfWeekCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ITEMS).elemMatch(
                        Criteria.where(ScheduleItem.getDayColumnname(dayOfWeek)).is(true));
            } else {
                dayOfWeekCriteria = Criteria.where(
                        Customer.COLUMNNAME_SCHEDULE_ITEM + "." + ScheduleItem.getDayColumnname(dayOfWeek)).is(true);
            }
        }

        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);

        Criteria criteria = CriteriaUtils.andOperator(distributorCriteria, scheduleNullCriteria, routeCriteria,
                dayOfWeekCriteria, searchCriteria);

        return _count(clientId, false, true, criteria);
    }

    @Override
    public List<Customer> getCustomersByRoutes(ObjectId clientId, Collection<ObjectId> routeIds, SimpleDate date) {
        Assert.notNull(routeIds);

        if (routeIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria routeCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ROUTE_ID).in(routeIds);

        Config config = configRepository.getConfig(clientId);

        Criteria dateCriteria = null;
        if (date != null) {
            int dayOfToday = date.getDayOfWeek();
            if (config.isComplexSchedule()) {
                Criteria weekCriteria = null;
                if (config.getNumberWeekOfFrequency() > 1) {
                    weekCriteria = Criteria.where(ScheduleItem.COLUMNNAME_WEEKS).all(
                            config.getWeekIndex(date));
                }

                dateCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ITEMS).elemMatch(
                        CriteriaUtils.andOperator(Criteria.where(ScheduleItem.getDayColumnname(dayOfToday)).is(true),
                                weekCriteria));
            } else {
                Criteria weekCriteria = null;
                if (config.getNumberWeekOfFrequency() > 1) {
                    weekCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ITEM_WEEKS).all(
                            config.getWeekIndex(date));
                }

                dateCriteria = CriteriaUtils.andOperator(
                        Criteria.where(
                                Customer.COLUMNNAME_SCHEDULE_ITEM + "." + ScheduleItem.getDayColumnname(dayOfToday))
                                .is(true), weekCriteria);
            }
        }

        Criteria criteria = CriteriaUtils.andOperator(routeCriteria, dateCriteria);

        return super._getList(clientId, false, true, criteria, null, null);
    }

    // CHECK
    @Override
    public boolean
            checkCustomerRoute(ObjectId clientId, Collection<ObjectId> customerIds, Collection<ObjectId> routeIds) {
        Assert.notEmpty(customerIds);
        Assert.notNull(routeIds);

        if (routeIds.isEmpty()) {
            return false;
        }

        Criteria idCriteria = Criteria.where(Customer.COLUMNNAME_ID).in(customerIds);
        Criteria routeCriteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ROUTE_ID).in(routeIds);

        return super._count(clientId, false, true, CriteriaUtils.andOperator(idCriteria, routeCriteria)) == customerIds
                .size();
    }

    @Override
    public boolean checkCustomerTypeUsed(ObjectId clientId, ObjectId customerTypeId) {
        Assert.notNull(customerTypeId);

        Criteria criteria = Criteria.where(Customer.COLUMNNAME_CUSTOMER_TYPE_ID).is(customerTypeId);

        return super._checkUsed(clientId, criteria);
    }

    @Override
    public boolean checkAreaUsed(ObjectId clientId, ObjectId areaId) {
        Assert.notNull(areaId);

        Criteria criteria = Criteria.where(Customer.COLUMNNAME_AREA_ID).is(areaId);

        return super._checkUsed(clientId, criteria);
    }

    @Override
    public boolean checkRouteUsed(ObjectId clientId, ObjectId routeId) {
        Assert.notNull(routeId);

        Criteria criteria = Criteria.where(Customer.COLUMNNAME_SCHEDULE_ROUTE_ID).is(routeId);

        return super._checkUsed(clientId, criteria);
    }

    // BATCH
    @Override
    public void insertCustomers(ObjectId clientId, Collection<Customer> customers) {
        super._insertBatch(clientId, customers);
    }

}
