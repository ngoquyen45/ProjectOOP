package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.DistributorCreateDto;
import com.viettel.backend.dto.category.DistributorDto;
import com.viettel.backend.dto.category.DistributorListDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableDistributorService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.DistributorService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

@RestController(value = "adminDistributorController")
@RequestMapping(value = "/admin/distributor")
public class DistributorController extends
        EditableCategoryController<DistributorListDto, DistributorDto, DistributorCreateDto> {

    @Autowired
    private EditableDistributorService editableDistributorService;
    
    @Autowired
    private DistributorService distributorService;

    @Override
    protected I_EditableCategoryService<DistributorListDto, DistributorDto, DistributorCreateDto> getEditableService() {
        return editableDistributorService;
    }

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return distributorService;
    }
    
    @Override
    protected boolean canSetActive() {
        return false;
    }

}