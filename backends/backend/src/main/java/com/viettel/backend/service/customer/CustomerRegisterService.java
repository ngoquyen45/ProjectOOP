package com.viettel.backend.service.customer;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.CustomerCreateDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.IdDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface CustomerRegisterService {
    
    public IdDto registerCustomer(UserLogin userLogin, CustomerCreateDto customerCreateDto, boolean autoApprove);
    
    public ListDto<CustomerListDto> getCustomersRegistered(UserLogin userLogin, String search, Pageable pageable);
    
}
