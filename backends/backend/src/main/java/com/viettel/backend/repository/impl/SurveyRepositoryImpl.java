package com.viettel.backend.repository.impl;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.Category;
import com.viettel.backend.domain.Survey;
import com.viettel.backend.repository.SurveyRepository;
import com.viettel.backend.util.CriteriaUtils;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;

@Repository
public class SurveyRepositoryImpl extends CategoryRepositoryImpl<Survey>implements SurveyRepository {

    @Override
    public List<Survey> getSurveysAvailable(ObjectId clientId) {
        SimpleDate today = DateTimeUtils.getToday();

        Criteria dateCriteria = CriteriaUtils.andOperator(
                Criteria.where(Survey.COLUMNNAME_START_DATE_VALUE).lte(today.getValue()),
                Criteria.where(Survey.COLUMNNAME_END_DATE_VALUE).gte(today.getValue()));

        Sort sort = new Sort(Direction.DESC, Survey.COLUMNNAME_START_DATE_VALUE);

        return super._getList(clientId, false, true, dateCriteria, null, sort);
    }

    @Override
    public List<Survey> getSurveysStarted(ObjectId clientId, String search, Pageable pageable, Sort sort) {
        SimpleDate today = DateTimeUtils.getToday();

        Criteria statedCriteria = Criteria.where(Survey.COLUMNNAME_START_DATE_VALUE).lte(today.getValue());
        
        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);
        
        Criteria criteria = CriteriaUtils.andOperator(statedCriteria, searchCriteria);
        
        return _getList(clientId, false, true, criteria, pageable, sort);
    }

    @Override
    public long countSurveysStarted(ObjectId clientId, String search) {
        SimpleDate today = DateTimeUtils.getToday();

        Criteria statedCriteria = Criteria.where(Survey.COLUMNNAME_START_DATE_VALUE).lte(today.getValue());
        
        Criteria searchCriteria = CriteriaUtils.getSearchLikeCriteria(Category.COLUMNNAME_SEARCH, search);
        
        Criteria criteria = CriteriaUtils.andOperator(statedCriteria, searchCriteria);
        
        return _count(clientId, false, true, criteria);
    }
    
}
