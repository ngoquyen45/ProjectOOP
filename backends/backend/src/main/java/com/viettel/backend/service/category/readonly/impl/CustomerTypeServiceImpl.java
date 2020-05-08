package com.viettel.backend.service.category.readonly.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CustomerTypeRepository;
import com.viettel.backend.service.category.readonly.CustomerTypeService;

@Service
public class CustomerTypeServiceImpl extends AbstractCategoryReadonlyService<CustomerType, CategorySimpleDto> implements
        CustomerTypeService {

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, CustomerType domain) {
        return new CategorySimpleDto(domain);
    }

    @Override
    public CategoryRepository<CustomerType> getRepository() {
        return customerTypeRepository;
    }

}
