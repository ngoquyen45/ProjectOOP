package com.viettel.backend.dto.category;

import java.util.ArrayList;
import java.util.List;

import com.viettel.backend.dto.common.CategoryCreateDto;

public class SurveyCreateDto extends CategoryCreateDto {

    private static final long serialVersionUID = 4294936505380930621L;
    
    private String startDate;
    private String endDate;
    private boolean required;
    private List<SurveyQuestionCreateDto> questions;
    
    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public List<SurveyQuestionCreateDto> getQuestions() {
        return questions;
    }

    public void setQuestions(List<SurveyQuestionCreateDto> questions) {
        this.questions = questions;
    }
 
    public static class SurveyQuestionCreateDto extends CategoryCreateDto {

        private static final long serialVersionUID = 5496584668724710642L;

        private boolean multipleChoice;
        private boolean required;

        private List<CategoryCreateDto> options;

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

        public List<CategoryCreateDto> getOptions() {
            return options;
        }

        public void setOptions(List<CategoryCreateDto> options) {
            this.options = options;
        }

        public void addOption(CategoryCreateDto option) {
            if (this.options == null) {
                this.options = new ArrayList<CategoryCreateDto>();
            }

            this.options.add(option);
        }
    }
    
}
