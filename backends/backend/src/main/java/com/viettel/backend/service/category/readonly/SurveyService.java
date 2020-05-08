package com.viettel.backend.service.category.readonly;

import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface SurveyService {

    public ListDto<SurveyDto> getSurveysAvailableByCustomer(UserLogin userLogin, String _customerId);

}
