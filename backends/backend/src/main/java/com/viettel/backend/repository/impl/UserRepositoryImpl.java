package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.repository.UserRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class UserRepositoryImpl extends BasicRepositoryImpl<User> implements UserRepository {

    private final static Sort DEFAULT_SORT = new Sort(User.COLUMNNAME_FULLNAME);

    @Override
    public User getDefaultAdmin(ObjectId clientId) {
        Criteria defaultAdmin = Criteria.where(User.COLUMNNAME_DEFAULT_ADMIN).is(true);
        return super._getFirst(clientId, null, null, defaultAdmin, null);
    }

    public List<User> getAdministrators(ObjectId clientId) {
        Criteria roleCriteria = Criteria.where(User.COLUMNNAME_ROLE).is(Role.ADMIN);
        return super._getList(clientId, false, true, roleCriteria, null, null);
    }

    @Override
    public List<User> getSupervisors(ObjectId clientId) {
        Criteria roleCriteria = Criteria.where(User.COLUMNNAME_ROLE).is(Role.SUPERVISOR);
        return super._getList(clientId, false, true, roleCriteria, null, null);
    }

    @Override
    public List<User> getSalesmenByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria roleCriteria = Criteria.where(User.COLUMNNAME_ROLE).is(Role.SALESMAN);

        Criteria distributorCriteria = Criteria.where(User.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);

        Criteria criteria = CriteriaUtils.andOperator(roleCriteria, distributorCriteria);

        return super._getList(clientId, false, true, criteria, null, DEFAULT_SORT);
    }

    @Override
    public List<User> getSalesmenByStoreCheckers(ObjectId clientId, ObjectId storeCheckerId) {
        Assert.notNull(storeCheckerId);

        Criteria roleCriteria = Criteria.where(User.COLUMNNAME_ROLE).is(Role.SALESMAN);

        Criteria storeCheckerCriteria = Criteria.where(User.COLUMNNAME_STORE_CHECKER_ID).is(storeCheckerId);

        Criteria criteria = CriteriaUtils.andOperator(roleCriteria, storeCheckerCriteria);

        return super._getList(clientId, false, true, criteria, null, DEFAULT_SORT);
    }

    @Override
    public List<User> getDistributorUsers(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Criteria roleCriteria = Criteria.where(User.COLUMNNAME_ROLE).is(Role.DISTRIBUTOR);

        Criteria distributorCriteria = null;
        if (distributorIds != null) {
            distributorCriteria = Criteria.where(User.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        }

        Criteria criteria = CriteriaUtils.andOperator(roleCriteria, distributorCriteria);

        return super._getList(clientId, false, true, criteria, null, DEFAULT_SORT);
    }

    @Override
    public void updateVanSalesStatus(ObjectId clientId, Collection<ObjectId> ids, boolean status) {
        if (ids != null && !ids.isEmpty()) {
            Update update = new Update();
            update.set(User.COLUMNNAME_VAN_SALES, status);
            super._updateMulti(clientId, null, null, ids, update);
        }
    }

    // ALL
    @Override
    public List<User> getList(ObjectId clientId, Boolean draft, Boolean active, String role, ObjectId distributorId,
            String search, Pageable pageable, Sort sort) {
        Criteria noDefaultAdmin = Criteria.where(User.COLUMNNAME_DEFAULT_ADMIN).ne(true);

        Criteria roleCriteria = null;
        if (role != null) {
            roleCriteria = CriteriaUtils.getSearchLikeCriteria(User.COLUMNNAME_ROLE, role);
        }

        Criteria distributorCriteria = null;
        if (distributorId != null) {
            distributorCriteria = Criteria.where(User.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);
        }

        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(User.COLUMNNAME_SEARCH, search);

        Criteria criteria = CriteriaUtils.andOperator(noDefaultAdmin, roleCriteria, distributorCriteria,
                searchCriteria);

        return _getList(clientId, draft, active, criteria, pageable, sort);
    }

    @Override
    public long count(ObjectId clientId, Boolean draft, Boolean active, String role, ObjectId distributorId,
            String search) {
        Criteria noDefaultAdmin = Criteria.where(User.COLUMNNAME_DEFAULT_ADMIN).ne(true);

        Criteria roleCriteria = null;
        if (role != null) {
            roleCriteria = CriteriaUtils.getSearchLikeCriteria(User.COLUMNNAME_ROLE, role);
        }

        Criteria distributorCriteria = null;
        if (distributorId != null) {
            distributorCriteria = Criteria.where(User.COLUMNNAME_DISTRIBUTOR_ID).is(distributorId);
        }
        
        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(User.COLUMNNAME_SEARCH, search);

        Criteria criteria = CriteriaUtils.andOperator(noDefaultAdmin, roleCriteria, distributorCriteria,
                searchCriteria);

        return _count(clientId, draft, active, criteria);
    }

    @Override
    public boolean checkUsernameExist(ObjectId clientId, ObjectId id, String username) {
        Criteria notMe = null;
        if (id != null) {
            notMe = Criteria.where(PO.COLUMNNAME_ID).ne(id);
        }

        Assert.notNull(username);
        Criteria usernameCriteria = CriteriaUtils.getSearchInsensitiveCriteria(User.COLUMNNAME_USERNAME, username);

        Criteria criteria = CriteriaUtils.andOperator(notMe, usernameCriteria);

        return super._exists(clientId, null, null, criteria);
    }

    @Override
    public boolean checkFullnameExist(ObjectId clientId, ObjectId id, String fullname) {
        Criteria notMe = null;
        if (id != null) {
            notMe = Criteria.where(PO.COLUMNNAME_ID).ne(id);
        }

        Assert.notNull(fullname);
        Criteria fullnameCriteria = CriteriaUtils.getSearchInsensitiveCriteria(User.COLUMNNAME_FULLNAME, fullname);

        Criteria criteria = CriteriaUtils.andOperator(notMe, fullnameCriteria);

        return super._exists(clientId, null, null, criteria);
    }

    // CHECK
    @Override
    public boolean checkStoreCheckerUsed(ObjectId clientId, ObjectId storeCheckerId) {
        Assert.notNull(storeCheckerId);

        Criteria criteria = Criteria.where(User.COLUMNNAME_STORE_CHECKER_ID).is(storeCheckerId);

        return super._exists(clientId, true, null, criteria) || super._exists(clientId, false, true, criteria);
    }

}
