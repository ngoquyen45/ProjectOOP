package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.ReadonlyCategoryController;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.ProductCategoryService;

@RestController(value = "distributorProductCategoryController")
@RequestMapping(value = "/distributor/product-category")
public class ProductCategoryController extends ReadonlyCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return productCategoryService;
    }

}
