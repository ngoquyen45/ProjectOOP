package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.PromotionCreateDto;
import com.viettel.backend.dto.category.PromotionDto;
import com.viettel.backend.dto.category.PromotionListDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditablePromotionService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;

@RestController(value = "adminPromotionController")
@RequestMapping(value = "/admin/promotion")
public class PromotionController
        extends EditableCategoryController<PromotionListDto, PromotionDto, PromotionCreateDto> {

    @Autowired
    private EditablePromotionService editablePromotionService;

    @Override
    protected I_EditableCategoryService<PromotionListDto, PromotionDto, PromotionCreateDto> getEditableService() {
        return editablePromotionService;
    }

    protected boolean canSetActive() {
        return false;
    }

}
