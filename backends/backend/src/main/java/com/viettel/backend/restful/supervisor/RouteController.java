package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.category.RouteCreateDto;
import com.viettel.backend.dto.category.RouteDto;
import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.EditableCategoryController;
import com.viettel.backend.service.category.editable.EditableRouteService;
import com.viettel.backend.service.category.editable.I_EditableCategoryService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.RouteService;

@RestController(value = "supervisorRouteController")
@RequestMapping(value = "/supervisor/route")
public class RouteController extends
        EditableCategoryController<RouteDto, RouteDto, RouteCreateDto> {

    @Autowired
    private EditableRouteService editableRouteService;

    @Autowired
    private RouteService routeService;

    @Override
    protected I_EditableCategoryService<RouteDto, RouteDto, RouteCreateDto> getEditableService() {
        return editableRouteService;
    }

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return routeService;
    }

}
