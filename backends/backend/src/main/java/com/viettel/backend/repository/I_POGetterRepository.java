package com.viettel.backend.repository;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.PO;

public interface I_POGetterRepository<D extends PO> {
    
    public Class<? extends PO> getDomainClass();
    
    // DRAFT = FALSE & ACTIVE = TRUE
    public D getById(ObjectId clientId, ObjectId id);
    
}
