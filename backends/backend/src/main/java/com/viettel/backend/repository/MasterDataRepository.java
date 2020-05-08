package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.User;

public interface MasterDataRepository {
    
    public List<User> getAllUsers(Object clientId);

    public void deleteDatas(Object clientId);
    
    public void deleteVisitAndOrder(Object clientId);
    
    public <D extends PO> void _insertBatch(ObjectId clientId, Class<D> clazz, Collection<D> domains);
    
}
