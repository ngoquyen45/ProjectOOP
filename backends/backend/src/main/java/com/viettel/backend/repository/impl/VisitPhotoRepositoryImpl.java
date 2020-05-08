package com.viettel.backend.repository.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.VisitPhoto;
import com.viettel.backend.repository.VisitPhotoRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.entity.SimpleDate.Period;

@Repository
public class VisitPhotoRepositoryImpl extends AbstractRepository<VisitPhoto> implements VisitPhotoRepository {

    @Override
    public List<VisitPhoto> getVisitPhoto(ObjectId clientId, ObjectId salesmanId, Period period) {
        Criteria visitCriteria = Criteria.where(Visit.COLUMNNAME_IS_VISIT).is(true);
        Criteria visitedCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);
        Criteria hasPhoto = Criteria.where(Visit.COLUMNNAME_PHOTO).ne(null);
        Criteria salesmanCriteria = Criteria.where(Visit.COLUMNNAME_SALESMAN_ID).is(salesmanId);
        Criteria periodCriteria = CriteriaUtils.getPeriodCriteria(Visit.COLUMNNAME_START_TIME_VALUE, period);

        Criteria criteria = CriteriaUtils.andOperator(visitCriteria, visitedCriteria, hasPhoto, salesmanCriteria,
                periodCriteria);

        Sort sort = new Sort(Visit.COLUMNNAME_START_TIME_VALUE);

        return _getList(clientId, false, true, criteria, null, sort);
    }

}
