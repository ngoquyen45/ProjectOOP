package com.viettel.backend.service.category.readonly;

import com.viettel.backend.dto.category.UserSimpleDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface UserService {
    
    public ListDto<UserSimpleDto> getSupervisors(UserLogin userLogin);
    
    public ListDto<UserSimpleDto> getSalesmen(UserLogin userLogin, String _distributorId);
    
}
