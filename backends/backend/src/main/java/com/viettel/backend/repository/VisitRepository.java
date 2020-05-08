package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface VisitRepository extends BasicRepository<Visit> {

    // ** ANY VISIT STATUS **
    public Visit getVisitByCustomerToday(ObjectId clientId, ObjectId customerId);

    /** Map by customerId */
    public Map<ObjectId, Visit> getMapVisitByCustomerIdsToday(ObjectId clientId, Collection<ObjectId> customerIds);

    /** check if any customer is this list is visiting */
    public boolean checkVisiting(ObjectId clientId, Collection<ObjectId> customerIds);

    // ** VISITED ONLY **
    public List<Visit> getVisitedsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, Period period,
            Pageable pageable, Sort sort);

    public long countVisitedsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, Period period);

    public List<Visit> getVisitedsByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period,
            Pageable pageable, Sort sort);

    public long countVisitedsByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds, Period period);

    public List<Visit> getVisitedsByCustomers(ObjectId clientId, Collection<ObjectId> customerIds, Period period,
            Sort sort);

    public List<Visit> getLastVisitedsByCustomer(ObjectId clientId, ObjectId customerId, int size, Period period);

}
