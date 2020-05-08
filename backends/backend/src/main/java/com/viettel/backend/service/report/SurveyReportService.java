package com.viettel.backend.service.report;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.report.SurveyResultDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface SurveyReportService {
    
    public ListDto<SurveyListDto> getSurveys(UserLogin userLogin, String search, Pageable pageable);
    
    public SurveyResultDto getSurveyReport(UserLogin userLogin, String surveyId);

    public byte[] exportSurveyReport(UserLogin userLogin, String surveyId, String lang);

}
