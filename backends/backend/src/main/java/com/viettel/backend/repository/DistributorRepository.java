package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Distributor;

public interface DistributorRepository extends CategoryRepository<Distributor> {
    
    public List<Distributor> getDistributorsBySupervisors(ObjectId clientId, Collection<ObjectId> supervisorIds);
    
    // CHECK
    public boolean checkSupervisorUsed(ObjectId clientId, ObjectId supervisorId);

}
