package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableProductCategoryService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.ProductCategoryService;

@RestController(value = "adminProductCategoryController")
@RequestMapping(value = "/admin/product-category")
public class ProductCategoryController extends
        EditableCategoryController<CategoryDto, CategoryDto, CategoryCreateDto> {

    @Autowired
    private EditableProductCategoryService editableProductCategoryService;

    @Autowired
    private ProductCategoryService productCategoryService;

    @Override
    protected I_EditableCategoryService<CategoryDto, CategoryDto, CategoryCreateDto> getEditableService() {
        return editableProductCategoryService;
    }

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return productCategoryService;
    }

}
