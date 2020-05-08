package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.Route;
import com.viettel.backend.repository.RouteRepository;

@Repository
public class RouteRepositoryImpl extends CategoryRepositoryImpl<Route>implements RouteRepository {

    @Override
    public List<Route> getAll(ObjectId clientId, Collection<ObjectId> distributorIds) {
        Criteria distributorCriteria = null;
        if (Category.isUseDistributor(getDomainClazz()) && distributorIds != null) {
            if (distributorIds.isEmpty()) {
                return Collections.emptyList();
            }
            
            distributorCriteria = Criteria.where(Category.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        }
        
        return _getList(clientId, false, true, distributorCriteria, null, null);
    }
    
    @Override
    public List<Route> getRoutesBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds) {
        Assert.notNull(salesmanIds);

        if (salesmanIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria salesmanCriteria = Criteria.where(Route.COLUMNNAME_SALESMAN_ID).in(salesmanIds);

        return super._getList(clientId, false, true, salesmanCriteria, null, null);
    }
    
    // CHECK
    @Override
    public boolean checkSalesmanUsed(ObjectId clientId, ObjectId salesmanId) {
        Assert.notNull(salesmanId);

        Criteria criteria = Criteria.where(Route.COLUMNNAME_SALESMAN_ID).is(salesmanId);

        return super._checkUsed(clientId, criteria);
    }

}
