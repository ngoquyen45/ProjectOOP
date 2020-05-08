package com.viettel.backend.repository.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import com.viettel.backend.domain.Visit;
import com.viettel.backend.domain.embed.SurveyAnswer;
import com.viettel.backend.repository.SurveyAnswerRepository;
import com.viettel.backend.util.CriteriaUtils;

@Repository
public class SurveyAnswerRepositoryImpl extends AbstractRepository<Visit> implements SurveyAnswerRepository {

    @Override
    protected Criteria getDefaultCriteria() {
        Criteria isVisitCriteria = Criteria.where(Visit.COLUMNNAME_IS_VISIT).is(true);
        Criteria visitStatusCriteria = Criteria.where(Visit.COLUMNNAME_VISIT_STATUS).is(Visit.VISIT_STATUS_VISITED);

        return CriteriaUtils.andOperator(isVisitCriteria, visitStatusCriteria);
    }

    @Override
    public List<SurveyAnswer> getSurveyAnswerByDistributors(ObjectId clientId, ObjectId surveyId,
            Collection<ObjectId> distributorIds) {
        Assert.notNull(surveyId);
        Assert.notNull(distributorIds);

        if (distributorIds.isEmpty()) {
            Collections.emptyList();
        }

        Criteria surveyCriteria = Criteria.where(Visit.COLUMNNAME_SURVEY_ANSWERS).elemMatch(
                Criteria.where(SurveyAnswer.COLUMNNAME_SURVEY_ID).is(surveyId));

        Criteria distributorCriteria = Criteria.where(Visit.COLUMNNAME_DISTRIBUTOR_ID).in(distributorIds);
        
        List<Visit> visits = _getList(clientId, false, true,
                CriteriaUtils.andOperator(surveyCriteria, distributorCriteria), null, null);
        
        List<SurveyAnswer> surveyAnswers = new ArrayList<SurveyAnswer>();
        for (Visit visit : visits) {
            if (visit.getSurveyAnswers() != null && !visit.getSurveyAnswers().isEmpty()) {
                for (SurveyAnswer surveyAnswer : visit.getSurveyAnswers()) {
                    surveyAnswer.setVisit(visit);
                    surveyAnswers.add(surveyAnswer);
                }
            }
        }

        return surveyAnswers;
    }

}
