package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.Route;

public interface RouteRepository extends CategoryRepository<Route> {
    
    public List<Route> getAll(ObjectId clientId, Collection<ObjectId> distributorIds);

    public List<Route> getRoutesBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds);
    
    // CHECK
    public boolean checkSalesmanUsed(ObjectId clientId, ObjectId salesmanId);
    
}
