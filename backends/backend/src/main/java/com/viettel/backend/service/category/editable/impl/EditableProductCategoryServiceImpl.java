package com.viettel.backend.service.category.editable.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.domain.User.Role;
import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.exeption.BusinessAssert;
import com.viettel.backend.exeption.BusinessExceptionCode;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.aspect.RolePermission;
import com.viettel.backend.service.category.editable.EditableProductCategoryService;

@RolePermission(value={ Role.ADMIN })
@Service
public class EditableProductCategoryServiceImpl extends
        AbstractCategoryEditableService<ProductCategory, CategoryDto, CategoryDto, CategoryCreateDto> implements
        EditableProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Override
    public CategoryRepository<ProductCategory> getRepository() {
        return productCategoryRepository;
    }

    @Override
    protected void beforeSetActive(UserLogin userLogin, ProductCategory domain, boolean active) {
        if (active) {
            // ACTIVE

        } else {
            // DEACTIVE
            BusinessAssert.notTrue(productRepository.checkProductCategoryUsed(userLogin.getClientId(), domain.getId()),
                    BusinessExceptionCode.RECORD_USED_IN_PRODUCT, "uom used in product");
        }
    }

    @Override
    public ProductCategory createDomain(UserLogin userLogin, CategoryCreateDto createdto) {
        ProductCategory domain = new ProductCategory();
        domain.setDraft(true);

        initPOWhenCreate(ProductCategory.class, userLogin, domain);

        return domain;
    }

    @Override
    public void updateDomain(UserLogin userLogin, ProductCategory domain, CategoryCreateDto createdto) {
        // Not allow update non draft record
        BusinessAssert.isTrue(domain.isDraft(), "Modify non-draft record are not allowed");
    }

    @Override
    public CategoryDto createListSimpleDto(UserLogin userLogin, ProductCategory domain) {
        return new CategoryDto(domain);
    }

    @Override
    public CategoryDto createListDetailDto(UserLogin userLogin, ProductCategory domain) {
        return new CategoryDto(domain);
    }

}
