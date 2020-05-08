package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableCustomerTypeService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.CustomerTypeService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

@RestController(value = "adminCustomerTypeController")
@RequestMapping(value = "/admin/customer-type")
public class CustomerTypeController extends
        EditableCategoryController<CategoryDto, CategoryDto, CategoryCreateDto> {

    @Autowired
    private EditableCustomerTypeService editableCustomerTypeService;

    @Autowired
    private CustomerTypeService customerTypeService;

    @Override
    protected I_EditableCategoryService<CategoryDto, CategoryDto, CategoryCreateDto> getEditableService() {
        return editableCustomerTypeService;
    }

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return customerTypeService;
    }

}
