package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableUOMService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.UOMService;

@RestController(value = "adminUOMController")
@RequestMapping(value = "/admin/uom")
public class UOMController extends
        EditableCategoryController<CategoryDto, CategoryDto, CategoryCreateDto> {

    @Autowired
    private EditableUOMService editableUOMService;
    
    @Autowired
    private UOMService uomService;

    @Override
    protected I_EditableCategoryService<CategoryDto, CategoryDto, CategoryCreateDto> getEditableService() {
        return editableUOMService;
    }
    
    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return uomService;
    }

}
