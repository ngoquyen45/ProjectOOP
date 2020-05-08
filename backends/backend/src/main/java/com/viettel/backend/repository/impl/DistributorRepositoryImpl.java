package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Distributor;
import com.viettel.backend.repository.DistributorRepository;

@Repository
public class DistributorRepositoryImpl extends CategoryRepositoryImpl<Distributor> implements DistributorRepository {

    @Override
    public List<Distributor> getDistributorsBySupervisors(ObjectId clientId, Collection<ObjectId> supervisorIds) {
        Assert.notNull(supervisorIds);
        
        if (supervisorIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        Criteria supervisorCriteria = Criteria.where(Distributor.COLUMNNAME_SUPERVISOR_ID).in(supervisorIds);
        
        return super._getList(clientId, false, true, supervisorCriteria, null, null);
    }

    // CHECK
    @Override
    public boolean checkSupervisorUsed(ObjectId clientId, ObjectId supervisorId) {
        Assert.notNull(supervisorId);

        Criteria criteria = Criteria.where(Distributor.COLUMNNAME_SUPERVISOR_ID).is(supervisorId);
        
        return super._checkUsed(clientId, criteria);
    }

}
