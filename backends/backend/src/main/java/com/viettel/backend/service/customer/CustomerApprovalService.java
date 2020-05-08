package com.viettel.backend.service.customer;

import org.springframework.data.domain.Pageable;

import com.viettel.backend.dto.category.CustomerDto;
import com.viettel.backend.dto.category.CustomerListDto;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;

public interface CustomerApprovalService {
    
    public ListDto<CustomerListDto> getPendingCustomers(UserLogin userLogin, Pageable pageable);

    public CustomerDto getCustomerById(UserLogin userLogin, String customerId);

    public void approve(UserLogin userLogin, String customerId);

    public void reject(UserLogin userLogin, String customerId);

}
