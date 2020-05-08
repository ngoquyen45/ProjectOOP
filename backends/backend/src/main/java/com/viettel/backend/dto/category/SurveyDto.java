package com.viettel.backend.dto.category;

import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.SurveyQuestion;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class SurveyDto extends SurveyListDto {

    private static final long serialVersionUID = 4294936505380930621L;

    private List<SurveyQuestionDto> questions;

    public SurveyDto(Survey survey) {
        super(survey);

        if (survey.getQuestions() != null) {
            for (SurveyQuestion surveyQuestion : survey.getQuestions()) {
                addQuestion(new SurveyQuestionDto(surveyQuestion));
            }
        }
    }

    public List<SurveyQuestionDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionDto> questions) {
        this.questions = questions;
    }

    public void addQuestion(SurveyQuestionDto question) {
        if (this.questions == null) {
            this.questions = new ArrayList<SurveyQuestionDto>();
        }

        this.questions.add(question);
    }
    
    public static class SurveyQuestionDto extends CategorySimpleDto {

        private static final long serialVersionUID = 5496584668724710642L;

        private boolean multipleChoice;
        private boolean required;

        private List<SurveyOptionDto> options;

        public SurveyQuestionDto(SurveyQuestion surveyQuestion) {
            super(surveyQuestion);

            this.multipleChoice = surveyQuestion.isMultipleChoice();
            this.required = surveyQuestion.isRequired();

            if (surveyQuestion.getOptions() != null) {
                for (CategoryEmbed surveyOption : surveyQuestion.getOptions()) {
                    addOption(new SurveyOptionDto(surveyOption));
                }
            }
        }

        public boolean isMultipleChoice() {
            return multipleChoice;
        }

        public void setMultipleChoice(boolean multipleChoice) {
            this.multipleChoice = multipleChoice;
        }

        public boolean isRequired() {
            return required;
        }

        public void setRequired(boolean required) {
            this.required = required;
        }

        public List<SurveyOptionDto> getOptions() {
            return options;
        }

        public void setOptions(List<SurveyOptionDto> options) {
            this.options = options;
        }

        public void addOption(SurveyOptionDto option) {
            if (this.options == null) {
                this.options = new ArrayList<SurveyOptionDto>();
            }

            this.options.add(option);
        }
    }
    
    public static class SurveyOptionDto extends CategorySimpleDto {

        private static final long serialVersionUID = 4085000556181222126L;

        public SurveyOptionDto(CategoryEmbed surveyOption) {
            super(surveyOption);
        }

    }

}
