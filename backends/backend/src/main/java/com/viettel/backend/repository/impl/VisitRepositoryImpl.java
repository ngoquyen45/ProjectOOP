package com.viettel.backend.repository.impl;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.repository.VisitRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class VisitRepositoryImpl extends BasicRepositoryImpl<Visit> implements VisitRepository {

    @Override
    protected Criteria getDefaultCriteria() {
        Criteria isVisitCriteria = Criteria.where(Visit.COLUMNNAME_IS_VISIT).is(true);
        return isVisitCriteria;
    }

    private Criteria getIndexTimeCriteria(Period period) {
        if (period == null) {
            throw new IllegalArgumentException("period is null");
        }

        long from = DateTimeUtils.truncate(period.getFromDate(), Calendar.DATE).getValue();
        long to = DateTimeUtils.addDays(DateTimeUtils.truncate(period.getToDate(), Calendar.DATE), 1).getValue();

        return Criteria.where(Visit.COLUMNNAME_START_TIME_VALUE).gte(from).lt(to);
    }

    // ** ANY VISIT STATUS **
    public Visit getVisitByCustomerToday(ObjectId clientId, ObjectId customerId) {
        Period todayPeriod = DateTimeUtils.getPeriodToday();

        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, todayPeriod);
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).is(customerId);

        Criteria criteria = CriteriaUtils.andOperator(dateCriteria, customerCriteria);

//        return super._getFirst(clientId, false, true, criteria, null);
        
        List<Visit> visits = super._getList(clientId, false, true, criteria, null, null);
        if (visits == null || visits.isEmpty()) {
            return null;
        } else if (visits.size() == 1) {
            return visits.get(0);
        } else {
            for (Visit visit : visits) {
                if (visit.getVisitStatus() == Visit.VISIT_STATUS_VISITED) {
                    return visit;
                }
            }
            return visits.get(0);
        }
    }

    public Map<ObjectId, Visit> getMapVisitByCustomerIdsToday(ObjectId clientId, Collection<ObjectId> customerIds) {
        Period todayPeriod = DateTimeUtils.getPeriodToday();

        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, todayPeriod);
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).in(customerIds);

        Criteria criteria = CriteriaUtils.andOperator(dateCriteria, customerCriteria);

        List<Visit> visits = super._getList(clientId, false, true, criteria, null, null);

        if (visits == null || visits.isEmpty()) {
            return Collections.<ObjectId, Visit> emptyMap();
        }

        Map<ObjectId, Visit> results = new HashMap<ObjectId, Visit>();
        for (Visit visit : visits) {
//            results.put(visit.getCustomer().getId(), visit);
            Visit tempVisit = results.get(visit.getCustomer().getId());
            if (tempVisit != null) {
                if (tempVisit.getVisitStatus() == Visit.VISIT_STATUS_VISITING
                        && visit.getVisitStatus() == Visit.VISIT_STATUS_VISITED) {
                    results.put(visit.getCustomer().getId(), visit);
                }
            } else {
                results.put(visit.getCustomer().getId(), visit);
            }

        }

        return results;
    }

    @Override
    public boolean checkVisiting(ObjectId clientId, Collection<ObjectId> customerIds) {
        Period todayPeriod = DateTimeUtils.getPeriodToday();

        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, todayPeriod);
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).in(customerIds);
        Criteria visitingCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITING);

        Criteria criteria = CriteriaUtils.andOperator(dateCriteria, customerCriteria, visitingCriteria);

        return super._exists(clientId, false, true, criteria);
    }

    // ** VISITED ONLY **
    @Override
    public List<Visit> getVisitedsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, Period period,
            Pageable pageable, Sort sort) {
        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria salesmanCriteria = Criteria.where(Visit.COLUMNNAME_SALESMAN_ID).in(salesmanIds);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, dateCriteria, visitStatusCriteria,
                salesmanCriteria);

        return super._getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countVisitedsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, Period period) {
        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria salesmanCriteria = Criteria.where(Visit.COLUMNNAME_SALESMAN_ID).in(salesmanIds);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, dateCriteria, visitStatusCriteria,
                salesmanCriteria);

        return super._count(clientId, false, true, criteria);
    }

    @Override
    public List<Visit> getVisitedsByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            Pageable pageable, Sort sort) {
        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria distributorCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, dateCriteria, visitStatusCriteria,
                distributorCriteria);

        return super._getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countVisitedsByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period) {
        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria distributorCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, dateCriteria, visitStatusCriteria,
                distributorCriteria);

        return super._count(clientId, false, true, criteria);
    }

    public List<Visit> getVisitedsByCustomers(ObjectId clientId, Collection<ObjectId> customerIds, Period period,
            Sort sort) {
        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria dateCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).in(customerIds);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, dateCriteria, visitStatusCriteria,
                customerCriteria);

        return super._getList(clientId, false, true, criteria, null, sort);
    }

    @Override
    public List<Visit> getLastVisitedsByCustomer(ObjectId clientId, ObjectId customerId, int size, Period period) {
        if (customerId == null) {
            return Collections.<Visit> emptyList();
        }

        Criteria indexTimeCriteria = getIndexTimeCriteria(period);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria customerCriteria = Criteria.where(Visit.COLUMNNAME_CUSTOMER_ID).is(customerId);

        Criteria criteria = CriteriaUtils.andOperator(indexTimeCriteria, visitStatusCriteria, customerCriteria);

        PageRequest pageable = new PageRequest(0, size, Direction.DESC, Visit.COLUMNNAME_START_TIME_VALUE);

        return super._getList(clientId, false, true, criteria, pageable, null);
    }

}
