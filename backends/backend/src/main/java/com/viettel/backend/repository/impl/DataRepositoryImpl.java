package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Data;
import com.viettel.backend.domain.PO;
import com.viettel.backend.repository.DataRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.entity.SimpleDate;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class DataRepositoryImpl implements DataRepository {

    @Autowired
    private MongoTemplate dataTemplate;

    @Override
    public List<Data> getDatas(ObjectId clientId, SimpleDate fromDate, Pageable pageable) {
        Assert.notNull(fromDate);

        Criteria clientCriteria = null;
        if (clientId != null) {
            clientCriteria = Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId);
        }

        Criteria activeCriteria = Criteria.where(PO.COLUMNNAME_ACTIVE).is(true);
        Criteria draftCriteria = Criteria.where(PO.COLUMNNAME_DRAFT).is(false);
        Criteria fromDateCriteria = Criteria.where(Data.COLUMNNAME_START_TIME_VALUE).gte(fromDate.getValue());

        Criteria criteria = CriteriaUtils.andOperator(clientCriteria, activeCriteria, draftCriteria, fromDateCriteria);

        Query mainQuery = new Query();
        mainQuery.addCriteria(criteria);
        if (pageable != null) {
            mainQuery.with(pageable);
        }

        return dataTemplate.find(mainQuery, Data.class);
    }

    @Override
    public long countDatas(ObjectId clientId, SimpleDate fromDate) {
        Assert.notNull(fromDate);

        Criteria clientCriteria = null;
        if (clientId != null) {
            clientCriteria = Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId);
        }

        Criteria activeCriteria = Criteria.where(PO.COLUMNNAME_ACTIVE).is(true);
        Criteria draftCriteria = Criteria.where(PO.COLUMNNAME_DRAFT).is(false);
        Criteria fromDateCriteria = Criteria.where(Data.COLUMNNAME_START_TIME_VALUE).gte(fromDate.getValue());

        Criteria criteria = CriteriaUtils.andOperator(clientCriteria, activeCriteria, draftCriteria, fromDateCriteria);

        Query mainQuery = new Query();
        mainQuery.addCriteria(criteria);

        return dataTemplate.count(mainQuery, Data.class);
    }

    @Override
    public List<Data> getDatasByCreated(ObjectId clientId, Collection<ObjectId> createdIds, Period period,
            Pageable pageable) {
        Assert.notNull(clientId);
        Assert.notNull(createdIds);
        Assert.notNull(period);

        Criteria clientCriteria = Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId);
        Criteria activeCriteria = Criteria.where(PO.COLUMNNAME_ACTIVE).is(true);
        Criteria draftCriteria = Criteria.where(PO.COLUMNNAME_DRAFT).is(false);
        Criteria periodCriteria = CriteriaUtils.getPeriodCriteria(Data.COLUMNNAME_START_TIME_VALUE, period);
        Criteria createdCriteria = Criteria.where(Data.COLUMNNAME_CREATED_BY_ID).in(createdIds);

        Criteria criteria = CriteriaUtils.andOperator(clientCriteria, activeCriteria, draftCriteria, periodCriteria,
                createdCriteria);

        Query mainQuery = new Query();
        mainQuery.addCriteria(criteria);
        if (pageable != null) {
            mainQuery.with(pageable);
        }

        return dataTemplate.find(mainQuery, Data.class);
    }

    @Override
    public long countDatasByCreated(ObjectId clientId, Collection<ObjectId> createdIds, Period period) {
        Assert.notNull(clientId);
        Assert.notNull(createdIds);
        Assert.notNull(period);

        Criteria clientCriteria = Criteria.where(PO.COLUMNNAME_CLIENT_ID).is(clientId);
        Criteria activeCriteria = Criteria.where(PO.COLUMNNAME_ACTIVE).is(true);
        Criteria draftCriteria = Criteria.where(PO.COLUMNNAME_DRAFT).is(false);
        Criteria periodCriteria = CriteriaUtils.getPeriodCriteria(Data.COLUMNNAME_START_TIME_VALUE, period);
        Criteria createdCriteria = Criteria.where(Data.COLUMNNAME_CREATED_BY_ID).in(createdIds);

        Criteria criteria = CriteriaUtils.andOperator(clientCriteria, activeCriteria, draftCriteria, periodCriteria,
                createdCriteria);

        Query mainQuery = new Query();
        mainQuery.addCriteria(criteria);

        return dataTemplate.count(mainQuery, Data.class);
    }

}
