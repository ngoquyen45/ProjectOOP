package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.ProductSimpleDto;
import com.viettel.backend.restful.ReadonlyCategoryController;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.ProductService;

@RestController(value = "supervisorProductController")
@RequestMapping(value = "/supervisor/product")
public class ProductController extends ReadonlyCategoryController {

    @Autowired
    private ProductService productService;

    @Override
    protected I_ReadonlyCategoryService<ProductSimpleDto> getReadonlyCategoryService() {
        return productService;
    }

}
