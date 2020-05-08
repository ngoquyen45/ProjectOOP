package com.viettel.backend.service.category.readonly;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

/**
 * return only draft = false and active = true
 */
public interface I_ReadonlyCategoryService<SIMPLE_DTO extends CategorySimpleDto> {
    
    public ListDto<SIMPLE_DTO> getAll(UserLogin userLogin, String distributorId);
    
    public Class<SIMPLE_DTO> getSimpleDtoClass();
    
}
