package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Customer;
import com.viettel.backend.repository.CustomerPendingRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class CustomerPendingRepositoryImpl extends AbstractRepository<Customer>implements CustomerPendingRepository {

    @Override
    public List<Customer> getCustomersByCreatedUsers(ObjectId clientId, Collection<ObjectId> userIds, Integer status,
            String search, Pageable pageable, Sort sort) {
        Assert.notNull(userIds);

        if (userIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria createdByCriteria = Criteria.where(Customer.COLUMNNAME_CREATED_BY_ID).in(userIds);

        Criteria statusCriteria = null;
        if (status != null) {
            statusCriteria = Criteria.where(Customer.COLUMNNAME_APPROVE_STATUS).is(status);
        }

        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Customer.COLUMNNAME_SEARCH, search);

        return super._getList(clientId, false, true,
                CriteriaUtils.andOperator(createdByCriteria, statusCriteria, searchCriteria), pageable, sort);
    }

    @Override
    public long countCustomersByCreatedUsers(ObjectId clientId, Collection<ObjectId> userIds, Integer status,
            String search) {
        Assert.notNull(userIds);

        if (userIds.isEmpty()) {
            return 0;
        }

        Criteria createdByCriteria = Criteria.where(Customer.COLUMNNAME_CREATED_BY_ID).in(userIds);

        Criteria statusCriteria = null;
        if (status != null) {
            statusCriteria = Criteria.where(Customer.COLUMNNAME_APPROVE_STATUS).is(status);
        }

        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Customer.COLUMNNAME_SEARCH, search);

        return super._count(clientId, false, true,
                CriteriaUtils.andOperator(createdByCriteria, statusCriteria, searchCriteria));
    }

    @Override
    public List<Customer> getPendingCustomersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds,
            Pageable pageable, Sort sort) {
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria distributorCriteria = Criteria.where(Customer.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria pendingCriteria = Criteria.where(Customer.COLUMNNAME_APPROVE_STATUS)
                .is(Customer.APPROVE_STATUS_PENDING);

        return super._getList(clientId, false, true, CriteriaUtils.andOperator(distributorCriteria, pendingCriteria),
                pageable, sort);
    }

    @Override
    public long countPendingCustomersByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return 0;
        }

        Criteria distributorCriteria = Criteria.where(Customer.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria pendingCriteria = Criteria.where(Customer.COLUMNNAME_APPROVE_STATUS)
                .is(Customer.APPROVE_STATUS_PENDING);
        
        return super._count(clientId, false, true,
                CriteriaUtils.andOperator(distributorCriteria, pendingCriteria));
    }

    @Override
    public Customer save(ObjectId clientId, Customer customer) {
        return super._save(clientId, customer);
    }

    @Override
    public Customer getById(ObjectId clientId, ObjectId id) {
        return super._getById(clientId, false, true, id);
    }

}
