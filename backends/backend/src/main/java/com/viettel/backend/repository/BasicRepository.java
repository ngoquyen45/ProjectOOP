package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.repository.NoRepositoryBean;

import com.viettel.backend.domain.PO;

@NoRepositoryBean
public interface BasicRepository<D extends PO> extends I_POGetterRepository<D> {

    public D save(ObjectId clientId, D domain);

    public boolean delete(ObjectId clientId, ObjectId id);
    
    // DRAFT = FALSE & ACTIVE = TRUE
    public List<D> getListByIds(ObjectId clientId, Collection<ObjectId> ids);

    // ALL
    public D getById(ObjectId clientId, Boolean draft, Boolean active, ObjectId id);

    public List<D> getListByIds(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids);

//    public Map<ObjectId, D> getMapByIds(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids);
    
    public boolean exists(ObjectId clientId, Boolean draft, Boolean active, ObjectId id);

    public boolean exists(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids);
    
}
