package com.viettel.backend.repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.viettel.backend.domain.Survey;

public interface SurveyRepository extends CategoryRepository<Survey> {

    public List<Survey> getSurveysAvailable(ObjectId clientId);
    
    public List<Survey> getSurveysStarted(ObjectId clientId, String search, Pageable pageable, Sort sort);
    
    public long countSurveysStarted(ObjectId clientId, String search);

}
