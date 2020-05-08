package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;

import com.viettel.backend.domain.PO;
import com.viettel.backend.repository.BasicRepository;

public abstract class BasicRepositoryImpl<D extends PO> extends AbstractRepository<D> implements BasicRepository<D> {

    @Override
    public D save(ObjectId clientId, D domain) {
        return _save(clientId, domain);
    }

    @Override
    public boolean delete(ObjectId clientId, ObjectId id) {
        return _delete(clientId, id);
    }

    // DRAFT = FALSE & ACTIVE = TRUE
    @Override
    public D getById(ObjectId clientId, ObjectId id) {
        return _getById(clientId, false, true, id);
    }

    @Override
    public List<D> getListByIds(ObjectId clientId, Collection<ObjectId> ids) {
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).in(ids);
        return _getList(clientId, false, true, criteria, null, null);
    }

    // ALL
    @Override
    public D getById(ObjectId clientId, Boolean draft, Boolean active, ObjectId id) {
        return _getById(clientId, draft, active, id);
    }

    @Override
    public List<D> getListByIds(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids) {
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).in(ids);
        return _getList(clientId, draft, active, criteria, null, null);
    }

//    @Override
//    public Map<ObjectId, D> getMapByIds(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids) {
//        List<D> domains = getListByIds(clientId, draft, active, ids);
//        return _getMap(domains);
//    }

    @Override
    public boolean exists(ObjectId clientId, Boolean draft, Boolean active, ObjectId id) {
        return _exists(clientId, draft, active, id);
    }

    @Override
    public boolean exists(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> ids) {
        Criteria criteria = Criteria.where(PO.COLUMNNAME_ID).in(ids);
        return _exists(clientId, draft, active, criteria);
    }

}
