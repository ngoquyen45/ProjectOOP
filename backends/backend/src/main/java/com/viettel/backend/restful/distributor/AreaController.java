package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategoryCreateDto;
import com.viettel.backend.dto.common.CategoryDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableAreaService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.AreaService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

@RestController(value = "distributorAreaController")
@RequestMapping(value = "/distributor/area")
public class AreaController extends EditableCategoryController<CategoryDto, CategoryDto, CategoryCreateDto> {

    @Autowired
    private EditableAreaService editableAreaService;

    @Autowired
    private AreaService areaService;

    @Override
    protected I_EditableCategoryService<CategoryDto, CategoryDto, CategoryCreateDto> getEditableService() {
        return editableAreaService;
    }

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return areaService;
    }

}
