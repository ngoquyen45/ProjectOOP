package com.viettel.backend.service.vansale;

import java.util.Map;

import com.viettel.backend.dto.category.UserDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface VanSalesService {

    public ListDto<UserDto> getSalesman(UserLogin userLogin, String distributorId);

    public void updateVanSalesStatus(UserLogin userLogin, String distributorId, Map<String, Boolean> vanSalesStatus);

}
