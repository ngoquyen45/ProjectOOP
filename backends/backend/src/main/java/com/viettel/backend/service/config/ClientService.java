package com.viettel.backend.service.config;

import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.config.ClientCreateDto;
import com.viettel.backend.dto.config.ClientDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;

public interface ClientService extends I_EditableCategoryService<CategoryDto, ClientDto, ClientCreateDto> {
    
    public void createSampleMasterData(UserLogin userLogin, String clientId);
    
    public void generateVisitAndOrder(UserLogin userLogin, String clientId);

}
