package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.util.entity.SimpleDate;

public interface CustomerRepository extends CategoryRepository<Customer> {

    public List<Customer> getCustomers(ObjectId clientId, ObjectId distributorId, boolean searchByRoute,
            ObjectId routeId, Integer dayOfWeek, String search, Pageable pageable, Sort sort);

    public long countCustomers(ObjectId clientId, ObjectId distributorId, boolean searchByRoute, ObjectId routeId,
            Integer dayOfWeek, String search);

    public List<Customer> getCustomersByRoutes(ObjectId clientId, Collection<ObjectId> routeIds, SimpleDate date);

    // CHECK
    public boolean checkCustomerRoute(ObjectId clientId, Collection<ObjectId> customerIds,
            Collection<ObjectId> routeIds);

    public boolean checkCustomerTypeUsed(ObjectId clientId, ObjectId customerTypeId);

    public boolean checkAreaUsed(ObjectId clientId, ObjectId areaId);

    public boolean checkDistributorUsed(ObjectId clientId, ObjectId distributorId);

    public boolean checkRouteUsed(ObjectId clientId, ObjectId routeId);

    // BATCH
    public void insertCustomers(ObjectId clientId, Collection<Customer> customers);

}
