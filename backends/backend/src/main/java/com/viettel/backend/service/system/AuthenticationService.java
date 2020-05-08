package com.viettel.backend.service.system;

import org.bson.types.ObjectId;

import com.viettel.backend.domain.User;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.system.ChangePasswordDto;
import com.viettel.backend.dto.system.UserInfoDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface AuthenticationService {

    public User getUserByUsername(String username);
    
    public CategoryDto getClient(ObjectId clientId);

    public UserInfoDto getUserInfoDto(UserLogin userLogin);

    public void changePassword(UserLogin userLogin, ChangePasswordDto changePasswordDto);

}
