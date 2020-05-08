package com.viettel.backend.dto.category;

import com.viettel.backend.domain.Survey;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.util.DateTimeUtils;
import com.viettel.backend.util.entity.SimpleDate;

public class SurveyListDto extends CategoryDto {

    private static final long serialVersionUID = 4294936505380930621L;
    
    private static final int STATUS_BEFORE_PROCESS = 0;
    private static final int STATUS_PROCESSING = 1;
    private static final int STATUS_PROCESSED = 2;

    private String startDate;
    private String endDate;
    private boolean required;
    private int dateStatus;
    
    public SurveyListDto(Survey survey) {
        super(survey);
        
        this.startDate = survey.getStartDate() != null ? survey.getStartDate().getIsoDate() : null;
        this.endDate = survey.getEndDate() != null ? survey.getEndDate().getIsoDate() : null;
        this.required = survey.isRequired();
        
        SimpleDate today = DateTimeUtils.getToday();

        if (survey.getStartDate().compareTo(today) > 0) {
            this.dateStatus = STATUS_BEFORE_PROCESS;
        } else {
            if (survey.getEndDate().compareTo(today) < 0) {
                this.dateStatus =  STATUS_PROCESSED;
            } else {
                this.dateStatus =  STATUS_PROCESSING;
            }
        }
    }

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

    public int getDateStatus() {
        return dateStatus;
    }
}
