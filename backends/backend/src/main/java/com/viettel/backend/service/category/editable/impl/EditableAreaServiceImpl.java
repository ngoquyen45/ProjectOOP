package com.viettel.backend.service.category.editable.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.Area;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.AreaRepository;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.CustomerRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableAreaService;

@RolePermission(value = { Role.ADMIN })
@Service
public class EditableAreaServiceImpl 
        extends AbstractCategoryEditableService<Area, CategoryDto, CategoryDto, CategoryCreateDto>
        implements EditableAreaService {

    @Autowired
    private AreaRepository areaRepository;
    
    @Autowired
    private CustomerRepository customerRepository;

    @Override
    public CategoryRepository<Area> getRepository() {
        return areaRepository;
    }
    
    @Override
    protected void beforeSetActive(UserLogin userLogin, Area domain, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DEACTIVE
            BusinessAssert.notTrue(customerRepository.checkAreaUsed(userLogin.getClientId(), domain.getId()),
                    BusinessExceptionCode.RECORD_USED_IN_CUSTOMER, "uom used in customer");
        }
    }

    @Override
    public Area createDomain(UserLogin userLogin, CategoryCreateDto createdto) {
        Area domain = new Area();
        domain.setDraft(true);

        initPOWhenCreate(Area.class, userLogin, domain);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, Area domain, CategoryCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");
    }

    @Override
    public CategoryDto createListSimpleDto(UserLogin userLogin, Area domain) {
        return new CategoryDto(domain);
    }

    @Override
    public CategoryDto createListDetailDto(UserLogin userLogin, Area domain) {
        return new CategoryDto(domain);
    }
    
}
