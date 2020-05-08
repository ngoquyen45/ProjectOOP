package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;

import com.viettel.backend.domain.Category;

@NoRepositoryBean
public interface CategoryRepository<D extends Category> extends BasicRepository<D> {

    // DRAFT = FALSE & ACTIVE = TRUE
    public List<D> getAll(ObjectId clientId, ObjectId distributorId);

    // ALL
    public List<D> getList(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> distributorIds,
            String search, Pageable pageable, Sort sort);

    public long count(ObjectId clientId, Boolean draft, Boolean active, Collection<ObjectId> distributorIds,
            String search);
    
    public boolean checkNameExist(ObjectId clientId, ObjectId id, String name, ObjectId distributorId);
    
    public boolean checkCodeExist(ObjectId clientId, ObjectId id, String code, ObjectId distributorId);
    
    public boolean checkDistributorUsed(ObjectId clientId, ObjectId distributorId);

}
