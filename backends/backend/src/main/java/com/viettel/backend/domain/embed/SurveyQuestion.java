package com.viettel.backend.domain.embed;

import java.util.List;

public class SurveyQuestion extends CategoryEmbed {

    private static final long serialVersionUID = 3227127774937212395L;

    private boolean multipleChoice;
    private boolean required;
    
    private List<CategoryEmbed> options;
    
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
    
    public List<CategoryEmbed> getOptions() {
        return options;
    }
    
    public void setOptions(List<CategoryEmbed> options) {
        this.options = options;
    }
    
}
