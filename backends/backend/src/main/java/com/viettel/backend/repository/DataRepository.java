package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;

import com.viettel.backend.domain.Data;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

public interface DataRepository {

    public List<Data> getDatas(ObjectId clientId, SimpleDate fromDate, Pageable pageable);
    
    public long countDatas(ObjectId clientId, SimpleDate fromDate);
    
    public List<Data> getDatasByCreated(ObjectId clientId, Collection<ObjectId> createdIds, Period period, Pageable pageable);
    
    public long countDatasByCreated(ObjectId clientId, Collection<ObjectId> createdIds, Period period);
    
}
