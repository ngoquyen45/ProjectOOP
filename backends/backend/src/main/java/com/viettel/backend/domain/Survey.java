package com.viettel.backend.domain;

import java.util.List;

import org.springframework.data.mongodb.core.mapping.Document;

import com.viettel.backend.domain.embed.SurveyQuestion;
import com.viettel.backend.util.entity.SimpleDate;

@Document(collection = "Survey")
public class Survey extends Category {

    private static final long serialVersionUID = 3227127774937212395L;

    public static final String COLUMNNAME_START_DATE_VALUE = "startDate.value";
    public static final String COLUMNNAME_END_DATE_VALUE = "endDate.value";
    public static final String COLUMNNAME_REQUIRED = "required";

    private SimpleDate startDate;
    private SimpleDate endDate;
    private boolean required;

    private List<SurveyQuestion> questions;

    public SimpleDate getStartDate() {
        return startDate;
    }

    public void setStartDate(SimpleDate startDate) {
        this.startDate = startDate;
    }

    public SimpleDate getEndDate() {
        return endDate;
    }

    public void setEndDate(SimpleDate endDate) {
        this.endDate = endDate;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<SurveyQuestion> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestion> questions) {
        this.questions = questions;
    }

}
