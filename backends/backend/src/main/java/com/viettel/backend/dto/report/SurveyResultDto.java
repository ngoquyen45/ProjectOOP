package com.viettel.backend.dto.report;

import java.util.List;

import com.viettel.backend.domain.Survey;
import com.viettel.backend.domain.embed.CategoryEmbed;
import com.viettel.backend.domain.embed.SurveyQuestion;
import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.dto.common.CategorySimpleDto;

public class SurveyResultDto extends SurveyListDto {

    private static final long serialVersionUID = 8542679375956025478L;
    
    private List<SurveyQuestionResultDto> questions;

    public SurveyResultDto(Survey survey, List<SurveyQuestionResultDto> questions) {
        super(survey);
        
        this.questions = questions;
    }

    public List<SurveyQuestionResultDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionResultDto> questions) {
        this.questions = questions;
    }

    public static class SurveyQuestionResultDto extends CategorySimpleDto {

        private static final long serialVersionUID = 5496584668724710642L;

        private boolean multipleChoice;
        private boolean required;
        private List<SurveyOptionResultDto> options;

        public SurveyQuestionResultDto(SurveyQuestion question, List<SurveyOptionResultDto> options) {
            super(question);

            this.multipleChoice = question.isMultipleChoice();
            this.required = question.isRequired();
            this.options = options;
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

        public List<SurveyOptionResultDto> getOptions() {
            return options;
        }

        public void setOptions(List<SurveyOptionResultDto> options) {
            this.options = options;
        }

    }

    public static class SurveyOptionResultDto extends CategorySimpleDto {

        private static final long serialVersionUID = 4085000556181222126L;

        private int result;

        public SurveyOptionResultDto(CategoryEmbed option, int result) {
            super(option);
            this.result = result;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

    }

}
