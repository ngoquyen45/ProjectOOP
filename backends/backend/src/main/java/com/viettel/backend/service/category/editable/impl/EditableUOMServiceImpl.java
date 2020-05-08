package com.viettel.backend.service.category.editable.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.UOM;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.repository.UOMRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableUOMService;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditableUOMServiceImpl extends
        AbstractCategoryEditableService<UOM, CategoryDto, CategoryDto, CategoryCreateDto> implements EditableUOMService {

    @Autowired
    private UOMRepository uomRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CategoryRepository<UOM> getRepository() {
        return uomRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, UOM domain, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DEACTIVE
            BusinessAssert.notTrue(productRepository.checkUOMUsed(userLogin.getClientId(), domain.getId()),
                    BusinessExceptionCode.RECORD_USED_IN_PRODUCT, "uom used in product");
        }
    }
    
    @Override
    public UOM createDomain(UserLogin userLogin, CategoryCreateDto createdto) {
        UOM uom = new UOM();
        uom.setDraft(true);

        initPOWhenCreate(UOM.class, userLogin, uom);

        return uom;
    }

    @Override
    public void updateDomain(UserLogin userLogin, UOM domain, CategoryCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");
    }

    @Override
    public CategoryDto createListSimpleDto(UserLogin userLogin, UOM domain) {
        return new CategoryDto(domain);
    }

    @Override
    public CategoryDto createListDetailDto(UserLogin userLogin, UOM domain) {
        return new CategoryDto(domain);
    }

}
