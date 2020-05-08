package com.viettel.backend.service.category.readonly.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductCategoryRepository;
import com.viettel.backend.service.category.readonly.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl extends AbstractCategoryReadonlyService<ProductCategory, CategorySimpleDto> implements
        ProductCategoryService {

    @Autowired
    private ProductCategoryRepository productCategoryRepository;

    @Override
    public CategorySimpleDto createSimpleDto(UserLogin userLogin, ProductCategory domain) {
        return new CategorySimpleDto(domain);
    }

    @Override
    public CategoryRepository<ProductCategory> getRepository() {
        return productCategoryRepository;
    }

}
