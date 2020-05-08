package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Customer;

public interface CustomerPendingRepository extends I_POGetterRepository<Customer> {

    public List<Customer> getCustomersByCreatedUsers(ObjectId clientId, Collection<ObjectId> userIds, Integer status,
            String search, Pageable pageable, Sort sort);

    public long countCustomersByCreatedUsers(ObjectId clientId, Collection<ObjectId> userIds, Integer status,
            String search);

    public List<Customer> getPendingCustomersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Pageable pageable, Sort sort);

    public long countPendingCustomersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds);
    
    public Customer save(ObjectId clientId, Customer customer);
    
}
