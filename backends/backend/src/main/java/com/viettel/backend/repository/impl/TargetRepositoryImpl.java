package com.viettel.backend.repository.impl;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Target;
import com.viettel.backend.repository.TargetRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class TargetRepositoryImpl extends BasicRepositoryImpl<Target>implements TargetRepository {

    @Override
    public List<Target> getTargetsBySalesmen(ObjectId clientId, Collection<ObjectId> salesmanIds, int month, int year,
            Sort sort) {
        Criteria dateCriteria = CriteriaUtils.andOperator(Criteria.where(Target.COLUMNNAME_MONTH).is(month),
                Criteria.where(Target.COLUMNNAME_YEAR).is(year));

        Criteria salesmanCriteria = Criteria.where(Target.COLUMNNAME_SALESMAN_ID).in(salesmanIds);

        return super._getList(clientId, false, true, CriteriaUtils.andOperator(salesmanCriteria, dateCriteria),
                null, sort);
    }

    @Override
    public boolean existsTargetBySalesman(ObjectId clientId, ObjectId salesmanId, int month, int year) {
        Criteria dateCriteria = CriteriaUtils.andOperator(Criteria.where(Target.COLUMNNAME_MONTH).is(month),
                Criteria.where(Target.COLUMNNAME_YEAR).is(year));

        Criteria salesmanCriteria = Criteria.where(Target.COLUMNNAME_SALESMAN_ID).is(salesmanId);

        return super._exists(clientId, false, true, CriteriaUtils.andOperator(salesmanCriteria, dateCriteria));
    }

    @Override
    public Target getTargetBySalesman(ObjectId clientId, ObjectId salesmanId, int month, int year) {
        Criteria dateCriteria = CriteriaUtils.andOperator(Criteria.where(Target.COLUMNNAME_MONTH).is(month),
                Criteria.where(Target.COLUMNNAME_YEAR).is(year));

        Criteria salesmanCriteria = Criteria.where(Target.COLUMNNAME_SALESMAN_ID).is(salesmanId);

        return super._getFirst(clientId, false, true, CriteriaUtils.andOperator(salesmanCriteria, dateCriteria), null);
    }

}
