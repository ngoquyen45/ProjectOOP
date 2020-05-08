package com.viettel.backend.dto.report;

import java.io.Serializable;

public class VisitResultDailyDto implements Serializable {

    private static final long serialVersionUID = 3352082909751004426L;

    private String date;
    private VisitResultDto visitResult;

    public VisitResultDailyDto(String date, VisitResultDto visitResult) {
        super();
        
        this.date = date;
        this.visitResult = visitResult;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public VisitResultDto getVisitResult() {
        return visitResult;
    }
    
    public void setVisitResult(VisitResultDto visitResult) {
        this.visitResult = visitResult;
    }

}