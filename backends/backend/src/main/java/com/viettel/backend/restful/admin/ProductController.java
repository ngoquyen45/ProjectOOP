package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.ProductCreateDto;
import com.viettel.backend.dto.category.ProductDto;
import com.viettel.backend.dto.category.ProductSimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableProductService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.ProductService;

@RestController(value = "adminProductController")
@RequestMapping(value = "/admin/product")
public class ProductController extends EditableCategoryController<ProductDto, ProductDto, ProductCreateDto> {

    @Autowired
    private EditableProductService editableProductService;

    @Autowired
    private ProductService productService;

    @Override
    protected I_EditableCategoryService<ProductDto, ProductDto, ProductCreateDto> getEditableService() {
        return editableProductService;
    }

    @Override
    protected I_ReadonlyCategoryService<ProductSimpleDto> getReadonlyCategoryService() {
        return productService;
    }

}
