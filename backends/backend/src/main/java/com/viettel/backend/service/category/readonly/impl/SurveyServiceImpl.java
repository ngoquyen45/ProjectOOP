package com.viettel.backend.service.category.readonly.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Survey;
import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.SurveyRepository;
import com.viettel.backend.service.category.readonly.SurveyService;
import com.viettel.backend.service.common.AbstractService;

@Service
public class SurveyServiceImpl extends AbstractService implements SurveyService {

    @Autowired
    private SurveyRepository surveyRepository;
    
    @Override
    public ListDto<SurveyDto> getSurveysAvailableByCustomer(UserLogin userLogin, String _customerId) {
        List<Survey> surveys = surveyRepository.getSurveysAvailable(userLogin.getClientId());
        List<SurveyDto> dtos = new ArrayList<SurveyDto>(surveys.size());
        for (Survey survey : surveys) {
            dtos.add(new SurveyDto(survey));
        }
        
        return new ListDto<SurveyDto>(dtos);
    }
    
}
