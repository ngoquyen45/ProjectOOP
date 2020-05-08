package com.viettel.backend.restful.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.ReadonlyCategoryController;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;
import com.viettel.backend.service.category.readonly.UOMService;

@RestController(value = "observerUOMController")
@RequestMapping(value = "/observer/uom")
public class UOMController extends ReadonlyCategoryController {

    @Autowired
    private UOMService uomService;
    
    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return uomService;
    }

}
