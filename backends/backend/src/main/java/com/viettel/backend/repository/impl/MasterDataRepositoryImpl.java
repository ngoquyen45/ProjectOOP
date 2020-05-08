package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.Customer;
import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.Data;
import com.viettel.backend.domain.Distributor;
import com.viettel.backend.domain.PO;
import com.viettel.backend.domain.Product;
import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.Promotion;
import com.viettel.backend.domain.Route;
import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User;
import com.viettel.backend.repository.MasterDataRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class MasterDataRepositoryImpl implements MasterDataRepository {

    @Autowired
    private MongoTemplate dataTemplate;

    @Override
    public List<User> getAllUsers(Object clientId) {
        Query clientQuery = new Query();
        clientQuery.addCriteria(Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId));
        return dataTemplate.find(clientQuery, User.class);
    }
    
    @Override
    public void deleteDatas(Object clientId) {
        Query userQuery = new Query();
        userQuery.addCriteria(CriteriaUtils.andOperator(Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId),
                Criteria.where(User.COLUMNNAME_DEFAULT_ADMIN).ne(true)));
        dataTemplate.remove(userQuery, User.class);

        Query clientQuery = new Query();
        clientQuery.addCriteria(Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId));
        dataTemplate.remove(clientQuery, Distributor.class);
        dataTemplate.remove(clientQuery, UOM.class);
        dataTemplate.remove(clientQuery, ProductCategory.class);
        dataTemplate.remove(clientQuery, Product.class);
        dataTemplate.remove(clientQuery, CustomerType.class);
        dataTemplate.remove(clientQuery, Area.class);
        dataTemplate.remove(clientQuery, Route.class);
        dataTemplate.remove(clientQuery, Customer.class);
        dataTemplate.remove(clientQuery, Promotion.class);
        dataTemplate.remove(clientQuery, Survey.class);
        dataTemplate.remove(clientQuery, Data.class);
    }
    
    @Override
    public void deleteVisitAndOrder(Object clientId) {
        Query clientQuery = new Query();
        clientQuery.addCriteria(Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId));
        dataTemplate.remove(clientQuery, Data.class);
    }

    @Override
    public <D extends PO> void _insertBatch(ObjectId clientId, Class<D> domainClazz, Collection<D> domains) {
        Assert.notNull(clientId);
        Assert.notNull(domainClazz);

        if (domains != null && !domains.isEmpty()) {
            dataTemplate.insert(domains, domainClazz);
        }
    }

}
