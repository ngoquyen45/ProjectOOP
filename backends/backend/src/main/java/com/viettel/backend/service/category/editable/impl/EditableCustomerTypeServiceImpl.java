package com.viettel.backend.service.category.editable.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.CustomerType;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.repository.CustomerTypeRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableCustomerTypeService;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditableCustomerTypeServiceImpl extends
        AbstractCategoryEditableService<CustomerType, CategoryDto, CategoryDto, CategoryCreateDto> implements
        EditableCustomerTypeService {

    @Autowired
    private CustomerTypeRepository customerTypeRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CategoryRepository<CustomerType> getRepository() {
        return customerTypeRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, CustomerType domain, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DEACTIVE
            BusinessAssert.notTrue(customerRepository.checkCustomerTypeUsed(userLogin.getClientId(), domain.getId()),
                    BusinessExceptionCode.RECORD_USED_IN_CUSTOMER, "uom used in customer");
        }
    }

    @Override
    public CustomerType createDomain(UserLogin userLogin, CategoryCreateDto createdto) {
        CustomerType domain = new CustomerType();
        domain.setDraft(true);

        initPOWhenCreate(CustomerType.class, userLogin, domain);

        updateDomain(userLogin, domain, createdto);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, CustomerType domain, CategoryCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");
    }

    @Override
    public CategoryDto createListSimpleDto(UserLogin userLogin, CustomerType domain) {
        return new CategoryDto(domain);
    }

    @Override
    public CategoryDto createListDetailDto(UserLogin userLogin, CustomerType domain) {
        return new CategoryDto(domain);
    }

}
