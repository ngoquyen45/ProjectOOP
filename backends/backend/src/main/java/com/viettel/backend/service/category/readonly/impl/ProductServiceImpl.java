package com.viettel.backend.service.category.readonly.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.viettel.backend.domain.Product;
import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.category.ProductSimpleDto;
import com.viettel.backend.dto.common.I_ProductPhotoFactory;
import com.viettel.backend.dto.common.ListDto;
import com.viettel.backend.oauth2.core.UserLogin;
import com.viettel.backend.repository.CategoryRepository;
import com.viettel.backend.repository.ProductRepository;
import com.viettel.backend.service.category.readonly.ProductService;

@Service
public class ProductServiceImpl extends AbstractCategoryReadonlyService<Product, ProductSimpleDto> implements
        ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public ProductSimpleDto createSimpleDto(UserLogin userLogin, Product domain) {
        return new ProductSimpleDto(domain, getProductPhotoFactory(userLogin), null);
    }

    @Override
    public CategoryRepository<Product> getRepository() {
        return productRepository;
    }

    @Override
    public ListDto<ProductDto> getList(UserLogin userLogin, String search, String _distributorId, Pageable pageable) {
        List<Product> domains = getRepository().getList(userLogin.getClientId(), false, true, null, search, pageable,
                null);
        if (CollectionUtils.isEmpty(domains) && pageable.getPageNumber() == 0) {
            return ListDto.emptyList();
        }

        I_ProductPhotoFactory productPhotoFactory = getProductPhotoFactory(userLogin);
        
        List<ProductDto> dtos = new ArrayList<ProductDto>(domains.size());
        for (Product domain : domains) {
            dtos.add(new ProductDto(domain, productPhotoFactory, null));
        }

        long size = Long.valueOf(dtos.size());
        if (pageable != null) {
            if (pageable.getPageNumber() > 0 || size == pageable.getPageSize()) {
                size = getRepository().count(userLogin.getClientId(), false, true, null, search);
            }
        }

        return new ListDto<ProductDto>(dtos, size);
    }

}
