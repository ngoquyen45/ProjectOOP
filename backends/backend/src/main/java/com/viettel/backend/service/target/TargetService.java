package com.viettel.backend.service.target;

import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.dto.target.TargetCreateDto;
import com.viettel.backend.dto.target.TargetDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface TargetService {

    public ListDto<TargetDto> getTargets(UserLogin userLogin, int month, int year);

    public TargetDto getTarget(UserLogin userLogin, int month, int year, String salesmanId);
    
    public IdDto save(UserLogin userLogin, TargetCreateDto dto);
    
    public boolean delete(UserLogin userLogin, int month, int year, String salesmanId);
    
}
