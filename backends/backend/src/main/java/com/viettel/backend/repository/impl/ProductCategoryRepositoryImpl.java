package com.viettel.backend.repository.impl;

import org.springframework.stereotype.Repository;

import com.viettel.backend.domain.ProductCategory;
import com.viettel.backend.repository.ProductCategoryRepository;

@Repository
public class ProductCategoryRepositoryImpl extends CategoryRepositoryImpl<ProductCategory> implements
        ProductCategoryRepository {

}
