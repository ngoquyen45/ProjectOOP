package com.viettel.backend.dto.visit;

import java.io.Serializable;

import com.viettel.backend.dto.common.ProgressDto;

public class VisitTodaySummary implements Serializable {

    private static final long serialVersionUID = -6227798543738734010L;
    
    private ProgressDto visit;
    private ProgressDto visitHasOrder;
    private ProgressDto visitErrorDuration;
    private ProgressDto visitErrorPosition;

    public ProgressDto getVisit() {
        return visit;
    }

    public void setVisit(ProgressDto visit) {
        this.visit = visit;
    }

    public ProgressDto getVisitHasOrder() {
        return visitHasOrder;
    }

    public void setVisitHasOrder(ProgressDto visitHasOrder) {
        this.visitHasOrder = visitHasOrder;
    }

    public ProgressDto getVisitErrorDuration() {
        return visitErrorDuration;
    }

    public void setVisitErrorDuration(ProgressDto visitErrorDuration) {
        this.visitErrorDuration = visitErrorDuration;
    }

    public ProgressDto getVisitErrorPosition() {
        return visitErrorPosition;
    }

    public void setVisitErrorPosition(ProgressDto visitErrorPosition) {
        this.visitErrorPosition = visitErrorPosition;
    }

}
