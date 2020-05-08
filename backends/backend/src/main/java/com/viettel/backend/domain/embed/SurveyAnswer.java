package com.viettel.backend.domain.embed;

import java.io.Serializable;
import java.util.Set;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Transient;

import com.viettel.backend.domain.Visit;

public class SurveyAnswer implements Serializable {

    private static final long serialVersionUID = 3227127774937212395L;

    public static final String COLUMNNAME_SURVEY_ID = "surveyId";
    public static final String COLUMNNAME_OPTIONS = "options";

    private ObjectId surveyId;
    private Set<ObjectId> options;
    
    @Transient
    private Visit visit;

    public ObjectId getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(ObjectId surveyId) {
        this.surveyId = surveyId;
    }

    public Set<ObjectId> getOptions() {
        return options;
    }

    public void setOptions(Set<ObjectId> options) {
        this.options = options;
    }
    
    public Visit getVisit() {
        return visit;
    }
    
    public void setVisit(Visit visit) {
        this.visit = visit;
    }

}
