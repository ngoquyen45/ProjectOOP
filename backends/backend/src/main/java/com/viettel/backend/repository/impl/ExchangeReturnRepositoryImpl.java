package com.viettel.backend.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapreduce.MapReduceResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.ExchangeReturn;
import com.viettel.backend.repository.ExchangeReturnRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class ExchangeReturnRepositoryImpl extends BasicRepositoryImpl<ExchangeReturn>
        implements ExchangeReturnRepository {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<ExchangeReturn> getListByCreatedBys(ObjectId clientId, boolean exchange,
            Collection<ObjectId> createdByIds, Period period, Pageable pageable, Sort sort) {
        Assert.notNull(createdByIds);
        if (createdByIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria exchangeCriteria = Criteria.where(ExchangeReturn.COLUMNNAME_EXCHANGE).is(exchange);
        Criteria createdByCriteria = Criteria.where(ExchangeReturn.COLUMNNAME_CREATED_BY_ID).in(createdByIds);
        Criteria periodCriteria = null;
        if (period != null) {
            periodCriteria = CriteriaUtils.getPeriodCriteria(ExchangeReturn.COLUMNNAME_CREATED_TIME_VALUE, period);
        }

        Criteria criteria = CriteriaUtils.andOperator(exchangeCriteria, createdByCriteria, periodCriteria);

        return _getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countByCreatedBys(ObjectId clientId, boolean exchange, Collection<ObjectId> createdByIds,
            Period period) {
        Assert.notNull(createdByIds);
        if (createdByIds.isEmpty()) {
            return 0;
        }

        Criteria exchangeCriteria = Criteria.where(ExchangeReturn.COLUMNNAME_EXCHANGE).is(exchange);
        Criteria createdByCriteria = Criteria.where(ExchangeReturn.COLUMNNAME_CREATED_BY_ID).in(createdByIds);
        Criteria periodCriteria = null;
        if (period != null) {
            periodCriteria = CriteriaUtils.getPeriodCriteria(ExchangeReturn.COLUMNNAME_CREATED_TIME_VALUE, period);
        }

        Criteria criteria = CriteriaUtils.andOperator(exchangeCriteria, createdByCriteria, periodCriteria);

        return _count(clientId, false, true, criteria);
    }

    @Override
    public List<ProductExchangeReturnQuanity> getProductIdQuantity(ObjectId clientId,
            Collection<ObjectId> distributorIds, Period period) {
        Assert.notNull(distributorIds);
        if (distributorIds.isEmpty()) {
            return Collections.emptyList();
        }

        Criteria ditributorCriteria = Criteria.where(ExchangeReturn.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        Criteria periodCriteria = null;
        if (period != null) {
            periodCriteria = CriteriaUtils.getPeriodCriteria(ExchangeReturn.COLUMNNAME_CREATED_TIME_VALUE, period);
        }

        Criteria criteria = CriteriaUtils.andOperator(ditributorCriteria, periodCriteria);

        Query query = initQuery(clientId, false, true, criteria, null, null);

        StringBuilder mapFn = new StringBuilder();
        mapFn.append("function () { ");
        mapFn.append("    for(var i = 0; i < this.details.length; i++) { ");
        mapFn.append("        var detail = this.details[i]; ");
        mapFn.append("        if (this.exchange) { ");
        mapFn.append(
                "            emit( detail.product._id, { exchangeQuantity: detail.quantity, returnQuantity: '0' } ); ");
        mapFn.append("        } else { ");
        mapFn.append(
                "            emit( detail.product._id, { exchangeQuantity: '0', returnQuantity: detail.quantity } ); ");
        mapFn.append("        } ");
        mapFn.append("    } ");
        mapFn.append("} ");

        StringBuilder reduceFn = new StringBuilder();
        reduceFn.append("function(key, values) { ");
        reduceFn.append("    var result = { exchangeQuantity: 0, returnQuantity: 0 }; ");
        reduceFn.append("    for(var i = 0; i < values.length; i++) { ");
        reduceFn.append("        var value = values[i]; ");
        reduceFn.append("        result.exchangeQuantity += parseFloat(value.exchangeQuantity); ");
        reduceFn.append("        result.returnQuantity += parseFloat(value.returnQuantity); ");
        reduceFn.append("    } ");
        reduceFn.append("    result.exchangeQuantity = result.exchangeQuantity.toString(); ");
        reduceFn.append("    result.returnQuantity = result.returnQuantity.toString(); ");
        reduceFn.append("    return result; ");
        reduceFn.append("}; ");

        MapReduceResults<ProductExchangeReturnQuanity> mapReduceResults = mongoTemplate.mapReduce(query,
                "ExchangeReturn", mapFn.toString(), reduceFn.toString(), ProductExchangeReturnQuanity.class);

        List<ProductExchangeReturnQuanity> list = new ArrayList<>();
        CollectionUtils.addAll(list, mapReduceResults.iterator());
        return list;
    }

}
