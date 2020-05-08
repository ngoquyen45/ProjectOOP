package com.viettel.backend.repository;

import java.util.Collection;
import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.embed.SurveyAnswer;

public interface SurveyAnswerRepository {

    public List<SurveyAnswer> getSurveyAnswerByDistributors(ObjectId clientId, ObjectId surveyId,
            Collection<ObjectId> distributorIds);

}
