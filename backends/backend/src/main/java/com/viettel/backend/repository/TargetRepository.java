package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Target;

public interface TargetRepository extends BasicRepository<Target> {

    public List<Target> getTargetsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, int month, int year,
            Sort sort);

    public boolean existsTargetBySalesman(ObjectId clientId, ObjectId salesmanId, int month, int year);

    public Target getTargetBySalesman(ObjectId clientId, ObjectId salesmanId, int month, int year);
    
}
