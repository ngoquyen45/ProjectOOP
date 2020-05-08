package com.viettel.backend.dto.report;

import java.io.Serializable;

import com.viettel.backend.dto.common.ProgressDto;

public class MobileDashboardDto implements Serializable {

    private static final long serialVersionUID = -7984406363915459531L;

    private ProgressDto revenue;
    private ProgressDto productivity;
    private ProgressDto visit;
    private ProgressDto day;

    public MobileDashboardDto(ProgressDto revenue, ProgressDto productivity, ProgressDto visit, ProgressDto day) {
        super();
        this.revenue = revenue;
        this.productivity = productivity;
        this.visit = visit;
        this.day = day;
    }

    public ProgressDto getRevenue() {
        return revenue;
    }

    public void setRevenue(ProgressDto revenue) {
        this.revenue = revenue;
    }

    public ProgressDto getProductivity() {
        return productivity;
    }

    public void setProductivity(ProgressDto productivity) {
        this.productivity = productivity;
    }

    public ProgressDto getVisit() {
        return visit;
    }

    public void setVisit(ProgressDto visit) {
        this.visit = visit;
    }

    public ProgressDto getDay() {
        return day;
    }

    public void setDay(ProgressDto day) {
        this.day = day;
    }

}
