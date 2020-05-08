package com.viettel.backend.dto.visit;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.embed.SurveyAnswer;

public class SurveyAnswerDto implements Serializable {

    private static final long serialVersionUID = 3227127774937212395L;

    private String surveyId;
    private List<String> options;

    public SurveyAnswerDto() {
        super();
    }

    public SurveyAnswerDto(SurveyAnswer surveyAnswer) {
        super();

        this.surveyId = surveyAnswer.getSurveyId().toString();
        if (surveyAnswer.getOptions() != null) {
            this.options = new ArrayList<String>(surveyAnswer.getOptions().size());
            for (ObjectId option : surveyAnswer.getOptions()) {
                this.options.add(option.toString());
            }
        }
    }

    public String getSurveyId() {
        return surveyId;
    }

    public void setSurveyId(String surveyId) {
        this.surveyId = surveyId;
    }

    public List<String> getOptions() {
        return options;
    }

    public void setOptions(List<String> options) {
        this.options = options;
    }

}
