package com.viettel.backend.restful.supervisor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.ReadonlyCategoryController;
import com.viettel.backend.service.category.readonly.CustomerTypeService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

@RestController(value = "supervisorCustomerTypeController")
@RequestMapping(value = "/supervisor/customer-type")
public class CustomerTypeController extends ReadonlyCategoryController {

    @Autowired
    private CustomerTypeService customerTypeService;

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return customerTypeService;
    }

}
