package com.viettel.backend.restful.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.SurveyCreateDto;
import com.viettel.backend.dto.category.SurveyDto;
import com.viettel.backend.dto.category.SurveyListDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableSurveyService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;

@RestController(value = "adminSurveyController")
@RequestMapping(value = "/admin/survey")
public class SurveyController extends EditableCategoryController<SurveyListDto, SurveyDto, SurveyCreateDto> {

    @Autowired
    private EditableSurveyService editableSurveyService;

    @Override
    protected I_EditableCategoryService<SurveyListDto, SurveyDto, SurveyCreateDto> getEditableService() {
        return editableSurveyService;
    }
    
    @Override
    protected boolean canSetActive() {
        return false;
    }

}
