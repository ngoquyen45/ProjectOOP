package com.viettel.backend.dto.report;

import java.io.Serializable;

public class SalesResultDailyDto implements Serializable {

    private static final long serialVersionUID = 3352082909751004426L;

    private String date;
    private SalesResultDto salesResult;

    public SalesResultDailyDto(String date, SalesResultDto salesResult) {
        super();
        
        this.date = date;
        this.salesResult = salesResult;
    }
    
    public String getDate() {
        return date;
    }
    
    public void setDate(String date) {
        this.date = date;
    }

    public SalesResultDto getSalesResult() {
        return salesResult;
    }
    
    public void setSalesResult(SalesResultDto salesResult) {
        this.salesResult = salesResult;
    }

}