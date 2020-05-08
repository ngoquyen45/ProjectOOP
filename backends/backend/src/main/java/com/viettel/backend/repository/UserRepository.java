package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.User;

/**
 * @author trungkh
 */
public interface UserRepository extends BasicRepository<User> {

    public User getDefaultAdmin(ObjectId clientId);

    public List<User> getAdministrators(ObjectId clientId);

    public List<User> getSupervisors(ObjectId clientId);

    public List<User> getSalesmenByDistributors(ObjectId clientId, Collection<ObjectId> distributorIds);

    public List<User> getSalesmenByStoreCheckers(ObjectId clientId, ObjectId storeCheckerId);

    public List<User> getDistributorUsers(ObjectId clientId, Collection<ObjectId> distributorId);

    public void updateVanSalesStatus(ObjectId clientId, Collection<ObjectId> ids, boolean status);

    // ALL
    public List<User> getList(ObjectId clientId, Boolean draft, Boolean active, String role, ObjectId distributorId,
            String search, Pageable pageable, Sort sort);

    public long count(ObjectId clientId, Boolean draft, Boolean active, String role, ObjectId distributorId,
            String search);

    public boolean checkUsernameExist(ObjectId clientId, ObjectId id, String username);

    public boolean checkFullnameExist(ObjectId clientId, ObjectId id, String username);

    // CHECK
    public boolean checkStoreCheckerUsed(ObjectId clientId, ObjectId storeCheckerId);

}
