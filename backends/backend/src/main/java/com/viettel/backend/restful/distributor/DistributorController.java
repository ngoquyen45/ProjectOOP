package com.viettel.backend.restful.distributor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.viettel.backend.dto.common.CategorySimpleDto;
import com.viettel.backend.restful.ReadonlyCategoryController;
import com.viettel.backend.service.category.readonly.DistributorService;
import com.viettel.backend.service.category.readonly.I_ReadonlyCategoryService;

@RestController(value = "distributorDistributorController")
@RequestMapping(value = "/distributor/distributor")
public class DistributorController extends ReadonlyCategoryController {

    @Autowired
    private DistributorService distributorService;

    @Override
    protected I_ReadonlyCategoryService<CategorySimpleDto> getReadonlyCategoryService() {
        return distributorService;
    }

}